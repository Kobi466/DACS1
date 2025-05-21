package view;

import controller.OrderController;
import dto.OrderDTO;
import dto.OrderItemDTO;
import dto.OrderSummaryDTO;
import network.GlobalResponseRouter;
import network.JsonResponse;
import util.JacksonUtils;
import util.OrderStatusRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderPanel extends JPanel {
    private final JTable orderTable, detailTable;
    private final DefaultTableModel orderModel, detailModel;
    private final JButton btnConfirm, btnCancel, btnComplete, btnRefresh, btCheBien;
    private final OrderController controller;

    public OrderPanel() {
        this.controller = new OrderController(this);
        this.setLayout(new BorderLayout());

        // ========== BẢNG ĐƠN HÀNG ==========
        orderModel = createOrderTableModel();
        orderTable = createOrderTable(orderModel);
        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // ========== PANEL DƯỚI ==========
        JPanel bottomPanel = new JPanel(new BorderLayout());

        detailModel = createDetailTableModel();
        detailTable = createDetailTable(detailModel);
        bottomPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        // ========== BUTTON PANEL ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnConfirm = createButton("✔ Xác nhận");
        btnCancel = createButton("✖ Huỷ");
        btnComplete = createButton("✓ Hoàn tất");
        btCheBien = createButton("Đang chế biến");
        btnRefresh = createButton("Làm mới", new Color(0x3498db), Color.WHITE);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnConfirm);
        buttonPanel.add(btCheBien);
        buttonPanel.add(btnComplete);
        buttonPanel.add(btnRefresh);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // ========== SỰ KIỆN ==========
        registerListeners();
        registerTableSelectionListener();
        registerButtonActions();

        controller.loadOrders();
    }

    // ====================== TABLE MODEL ======================

    private DefaultTableModel createOrderTableModel() {
        return new DefaultTableModel(
                new Object[]{"ID", "Tên Khách", "SĐT", "Thời gian", "Trạng thái"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho chỉnh sửa trực tiếp
            }
        };
    }

    private JTable createOrderTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.setDefaultRenderer(Object.class, new OrderStatusRenderer());
        return table;
    }

    private DefaultTableModel createDetailTableModel() {
        return new DefaultTableModel(new Object[]{"Món ăn", "SL", "Đơn giá", "Tổng"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createDetailTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        return table;
    }

    // ====================== BUTTON ======================

    private JButton createButton(String text) {
        return createButton(text, null, null);
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        if (bg != null) {
            button.setBackground(bg);
            button.setForeground(fg);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setFocusPainted(false);
        }
        return button;
    }

    // ====================== SỰ KIỆN ======================

    private void registerListeners() {
        GlobalResponseRouter.addListener(response -> {
            switch (response.getStatus()) {
                case "GET_ORDERS_SUCCESS" -> {
                    List<OrderSummaryDTO> orders = (List<OrderSummaryDTO>) response.getData();
                    controller.setOrders(orders);
                    updateOrderTable(orders);
                    System.out.println("✅ Đã nhận danh sách đơn hàng.");
                }
                case "NEW_ORDER_CREATED" -> {
                    OrderSummaryDTO newOrder = JacksonUtils.getObjectMapper().convertValue(
                            response.getData(), OrderSummaryDTO.class);
                    controller.addOrder(newOrder);
                    updateOrderTable(controller.getOrders());
                    controller.loadOrderDetail(newOrder.getOrderId());
                    System.out.println("🆕 Đơn hàng mới đã được thêm.");
                    controller.reloadOrders();
                }
                case "GET_ORDER_ITEMS_SUCCESS" -> {
                    OrderDTO order = JacksonUtils.getObjectMapper().convertValue(
                            response.getData(), OrderDTO.class);
                    updateOrderDetail(order);
                    System.out.println("✅ Đã nhận chi tiết đơn hàng.");
                }
                case "UPDATE_ORDER_STATUS" -> {
                    OrderSummaryDTO order = JacksonUtils.getObjectMapper().convertValue(
                            response.getData(), OrderSummaryDTO.class);
                    controller.updateOrderStatus(order.getOrderId(), order.getStatus());
                    controller.reloadOrders();
                }
                case "UPDATE_ORDER_STATUS_SUCCESS" -> {
                    JOptionPane.showMessageDialog(null, "✅ Cập nhật trạng thái thành công.");
                }
                case "UPDATE_ORDER_STATUS_FAIL" -> {
                    JOptionPane.showMessageDialog(null, "❌ Cập nhật trạng thái thất bại.");
                }
                default -> System.out.println("⚠️ Không hiểu phản hồi: " + response.getStatus());
            }
        });
    }

    private void registerTableSelectionListener() {
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Integer orderId = getSelectedOrderId();
                if (orderId != null) {
                    controller.loadOrderDetail(orderId);
                } else {
                    detailModel.setRowCount(0);
                }
            }
        });
    }

    private void registerButtonActions() {
        // Làm mới danh sách
        btnRefresh.addActionListener(e -> controller.reloadOrders());

        // Xác nhận đơn hàng: CHỜ XÁC NHẬN -> ĐÃ XÁC NHẬN
        btnConfirm.addActionListener(e -> {
            OrderSummaryDTO order = getSelectedOrder();
            if (order == null) return;

            if (order.getStatus() == OrderSummaryDTO.OrderStatus.CHO_XAC_NHAN) {
                controller.updateOrderStatus(order.getOrderId(), OrderSummaryDTO.OrderStatus.DA_XAC_NHAN);
            } else {
                showError("❌ Chỉ có thể xác nhận đơn đang ở trạng thái 'Chờ xác nhận'.");
            }
        });

        // Chế biến đơn hàng: ĐÃ XÁC NHẬN -> ĐANG CHẾ BIẾN
        btCheBien.addActionListener(e -> {
            OrderSummaryDTO order = getSelectedOrder();
            if (order == null) return;

            if (order.getStatus() == OrderSummaryDTO.OrderStatus.DA_XAC_NHAN) {
                controller.updateOrderStatus(order.getOrderId(), OrderSummaryDTO.OrderStatus.DANG_CHE_BIEN);
            } else {
                showError("❌ Chỉ có thể chuyển sang 'Đang chế biến' từ đơn 'Đã xác nhận'.");
            }
        });

        // Hoàn tất đơn hàng: ĐANG CHẾ BIẾN -> HOÀN THÀNH
        btnComplete.addActionListener(e -> {
            OrderSummaryDTO order = getSelectedOrder();
            if (order == null) return;

            if (order.getStatus() == OrderSummaryDTO.OrderStatus.DANG_CHE_BIEN) {
                controller.updateOrderStatus(order.getOrderId(), OrderSummaryDTO.OrderStatus.HOAN_THANH);
            } else {
                showError("❌ Chỉ có thể hoàn tất đơn đang ở trạng thái 'Đang chế biến'.");
            }
        });

        // Huỷ đơn hàng: chỉ cho huỷ nếu đang CHỜ XÁC NHẬN hoặc ĐÃ XÁC NHẬN
        btnCancel.addActionListener(e -> {
            OrderSummaryDTO order = getSelectedOrder();
            if (order == null) return;

            OrderSummaryDTO.OrderStatus status = order.getStatus();
            if (status == OrderSummaryDTO.OrderStatus.CHO_XAC_NHAN ||
                    status == OrderSummaryDTO.OrderStatus.DA_XAC_NHAN) {
                controller.updateOrderStatus(order.getOrderId(), OrderSummaryDTO.OrderStatus.DA_HUY);
            } else {
                showError("❌ Chỉ có thể huỷ đơn ở trạng thái 'Chờ xác nhận' hoặc 'Đã xác nhận'.");
            }
        });
    }


    // ====================== CẬP NHẬT GIAO DIỆN ======================

    public void updateOrderTable(List<OrderSummaryDTO> orders) {
        SwingUtilities.invokeLater(() -> {
            orderModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (OrderSummaryDTO o : orders) {
                // Thay đổi màu sắc hoặc định dạng của trạng thái dựa vào trạng thái của đơn hàng
                String statusDisplay = switch (o.getStatus()) {
                    case CHO_XAC_NHAN -> "🟡 Chờ xác nhận";
                    case DA_XAC_NHAN -> "🟢 Đã xác nhận";
                    case DANG_CHE_BIEN -> "🔵 Đang chế biến";
                    case HOAN_THANH -> "✅ Hoàn thành";
                    case DA_HUY -> "❌ Đã hủy";
                };

                // Thêm dữ liệu vào bảng
                orderModel.addRow(new Object[]{
                        o.getOrderId(),                             // ID
                        o.getCustomerName(),                        // Tên khách
                        o.getCustomerPhone(),                       // Số điện thoại khách
                        o.getOrderDate().format(fmt),               // Thời gian
                        statusDisplay,                              // Hiển thị trạng thái
                        String.format("%,.2f VNĐ", o.getTotalPrice()) // Hiển thị số tiền
                });
            }
        });
    }

    public void updateOrderDetail(OrderDTO order) {
        SwingUtilities.invokeLater(() -> {
            detailModel.setRowCount(0); // Xóa dữ liệu cũ

            // Hiển thị từng sản phẩm trong bảng chi tiết đơn hàng
            for (OrderItemDTO item : order.getItems()) {
                detailModel.addRow(new Object[]{
                        item.getFoodName(),                      // Tên món ăn
                        item.getQuantity(),                      // Số lượng
                        String.format("%,.2f VNĐ", item.getUnitPrice()),  // Đơn giá
                        String.format("%,.2f VNĐ", item.getTotalPrice())  // Tổng giá
                });
            }
            detailModel.addRow(new Object[]{
                    "Tổng tiền:", "", "", String.format("%,.2f VNĐ", order.getTotalPrice())
            });
        });
    }

    // Hàm định dạng trạng thái (dựa vào trạng thái OrderSummaryDTO.OrderStatus)
    private String formatOrderStatus(int orderId) {
        OrderSummaryDTO orderSummary = controller.getOrderById(orderId);
        if (orderSummary == null) return "Không xác định";

        return switch (orderSummary.getStatus()) {
            case CHO_XAC_NHAN -> "🟡 Chờ xác nhận";
            case DA_XAC_NHAN -> "🟢 Đã xác nhận";
            case DANG_CHE_BIEN -> "🔵 Đang chế biến";
            case HOAN_THANH -> "✅ Hoàn thành";
            case DA_HUY -> "❌ Đã hủy";
        };
    }

    // ====================== TIỆN ÍCH ======================

    private Integer getSelectedOrderId() {
        int row = orderTable.getSelectedRow();
        if (row >= 0) {
            Object value = orderModel.getValueAt(orderTable.convertRowIndexToModel(row), 0);
            if (value instanceof Integer) return (Integer) value;
            try {
                return Integer.parseInt(value.toString());
            } catch (Exception e) {
                showError("Lỗi lấy ID đơn hàng.");
            }
        }
        return null;
    }

    private OrderSummaryDTO getSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row >= 0) {
            int modelRow = orderTable.convertRowIndexToModel(row);
            int orderId = (int) orderModel.getValueAt(modelRow, 0);
            return controller.getOrderById(orderId);
        }
        showError("Vui lòng chọn một đơn hàng.");
        return null;
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}
