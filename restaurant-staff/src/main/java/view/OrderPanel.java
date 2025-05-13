package view;

import controller.OrderController;
import dto.OrderDTO;
import dto.OrderItemDTO;
import dto.OrderSummaryDTO;
import network.GlobalResponseRouter;
import network.JsonResponse;
import util.JacksonUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderPanel extends JPanel {
    private JTable table, detailTable;
    private DefaultTableModel model, detailModel;
    private final JButton btnXacNhan, btnHuy, btnHoanThanh;
    private final OrderController controller;

    public OrderPanel() {
        this.controller = new OrderController(this);
        this.setLayout(new BorderLayout());

        // === BẢNG ĐƠN HÀNG ===
        model = new DefaultTableModel(new Object[]{"ID", "Tên Khách", "SĐT", "Thời gian", "Trạng thái"}, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // === PANEL DƯỚI: CHI TIẾT + NÚT ===
        JPanel bottomPanel = new JPanel(new BorderLayout());

        detailModel = new DefaultTableModel(new Object[]{"Món ăn", "SL", "Đơn giá", "Tổng"}, 0);
        detailTable = new JTable(detailModel);
        detailTable.setRowHeight(28);
        bottomPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        // === PANEL BUTTON ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnXacNhan = new JButton("✔ Xác nhận");
        btnHuy = new JButton("✖ Huỷ");
        btnHoanThanh = new JButton("✓ Hoàn tất");
        buttonPanel.add(btnXacNhan);
        buttonPanel.add(btnHuy);
        buttonPanel.add(btnHoanThanh);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(new Color(0x3498db));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));

// Thêm vào panel header
        buttonPanel.add(refreshButton);
        refreshButton.addActionListener(e -> {
            controller.reloadOrders(); // gọi controller xử lý làm mới
        });

        // === ĐĂNG KÝ LẮNG NGHE + ACTION ===
        registerListeners();
        registerButtonActions();
        registerTableSelection();

        controller.loadOrders();
    }
    public int getSelectedOrderRow() {
        return table.getSelectedRow();
    }

    public OrderSummaryDTO getOrderAt(int row) {
        int modelRow = table.convertRowIndexToModel(row);
        Object value = model.getValueAt(modelRow, 0); // lấy orderId
        if (value instanceof Integer) {
            int orderId = (Integer) value;
            for (OrderSummaryDTO o : controller.getOrders()) {
                if (o.getOrderId() == orderId) return o;
            }
        }
        return null;
    }
    private void registerListeners() {
        GlobalResponseRouter.addListener(response -> {
            switch (response.getStatus()) {
                case "GET_ORDERS_SUCCESS" -> {
                    List<OrderSummaryDTO> orders = (List<OrderSummaryDTO>) response.getData();
                    controller.setOrders(orders); // thêm dòng này
                    updateOrderTable(orders);
                    System.out.println("✅ Đã nhận danh sách đơn hàng.");
                }
                case "NEW_ORDER_CREATED" -> {
                    OrderSummaryDTO newOrder = JacksonUtils.getObjectMapper().convertValue(
                            response.getData(), OrderSummaryDTO.class
                    );

                    controller.addOrder(newOrder);
                    updateOrderTable(controller.getOrders());

                    controller.loadOrderDetail(newOrder.getOrderId());


                    System.out.println("🆕 Đơn hàng mới đã được thêm: " + newOrder.getCustomerName());
                }case "GET_ORDER_ITEMS_SUCCESS" -> {
                    OrderDTO order = JacksonUtils.getObjectMapper().convertValue(
                            response.getData(), OrderDTO.class
                    );
                    System.out.println("📦 order.getItems().size = " + order.getItems().size()); // debug
                    updateOrderDetail(order);
                    System.out.println("✅ Đã nhận chi tiết đơn hàng.");
                }

                default -> System.out.println("⚠️ Không hiểu phản hồi: " + response.getStatus());
            }
        });
    }

    private void registerTableSelection() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SwingUtilities.invokeLater(() -> {
                    int viewRow = table.getSelectedRow();
                    System.out.println(">>> selection listener fired, viewRow=" + viewRow);
                    Integer orderId = getSelectedOrderId();
                    System.out.println(">>> getSelectedOrderId() => " + orderId);
                    if (orderId != null) {
                        System.out.println(">>> calling controller.loadOrderDetail(" + orderId + ")");
                        controller.loadOrderDetail(orderId);
                    } else {
                        detailModel.setRowCount(0);
                    }
                });
            }
        });
    }

    private void registerButtonActions() {
        btnXacNhan.addActionListener(e -> {
            Integer orderId = getSelectedOrderId();
//            if (orderId != null) controller.updateOrderStatus(orderId, "CONFIRMED");
        });

        btnHuy.addActionListener(e -> {
            Integer orderId = getSelectedOrderId();
//            if (orderId != null) controller.updateOrderStatus(orderId, "CANCELLED");
        });

        btnHoanThanh.addActionListener(e -> {
            Integer orderId = getSelectedOrderId();
//            if (orderId != null) controller.updateOrderStatus(orderId, "COMPLETED");
        });
    }

    private Integer getSelectedOrderId() {
        int viewRow = table.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = table.convertRowIndexToModel(viewRow); // CHÍNH XÁC hơn
            Object value = model.getValueAt(modelRow, 0); // Cột 0 là orderId
            if (value instanceof Integer) return (Integer) value;
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                showError("Không lấy được mã đơn hàng.");
            }
        }
        return null;
    }

    public void updateOrderTable(List<OrderSummaryDTO> orders) {
        OrderSummaryDTO orderSummaryDTO = new OrderSummaryDTO();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        String formattedTime = orderSummaryDTO.getOrderDate().format(formatter);
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            for (OrderSummaryDTO order : orders) {
                model.addRow(new Object[]{
                        order.getOrderId(),
                        order.getCustomerName(),
                        order.getCustomerPhone(),
                        order.getOrderDate(),
                        order.getStatus().name()
                });
            }
        });
    }

    private void addOrderToTable(OrderSummaryDTO order) {
        SwingUtilities.invokeLater(() -> {
            model.addRow(new Object[]{
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getCustomerPhone(),
                    order.getOrderDate(),
                    order.getStatus().name()
            });
        });
    }

    public void updateOrderDetail(OrderDTO order) {
        SwingUtilities.invokeLater(() -> {
            detailModel.setRowCount(0);
            for (OrderItemDTO item : order.getItems()) {
                detailModel.addRow(new Object[]{
                        item.getFoodName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                });
            }
        });
    }
    public void updateOrderItemTable(List<OrderItemDTO> items) {
        DefaultTableModel model = (DefaultTableModel) detailTable.getModel();
        detailModel.setRowCount(0); // 🔴 clear bảng trước

        for (OrderItemDTO item : items) {
            model.addRow(new Object[]{
                    item.getFoodName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotalPrice()
            });
        }
    }


    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
