package view.restaurantView;

import dao.OrderDAO;
import model.Order;
import model.OrderItem;
import model.TableBooking;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

public class OrderPanel extends JPanel {
    private JTable orderTable, detailTable;
    private DefaultTableModel orderModel, detailModel;
    private JTextField searchField;
    private JComboBox<Order.OrderStatus> statusFilter;
    private JLabel statsLabel;
    private List<Order> allOrders = new ArrayList<>();
    private Timer refreshTimer;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private Long selectedOrderId = null;
    private boolean isUserInteracting = false;private static final Map<Order.OrderStatus, Color> STATUS_COLORS = Map.of(
            Order.OrderStatus.CHO_XAC_NHAN, new Color(255, 255, 200),
            Order.OrderStatus.DA_XAC_NHAN, new Color(200, 255, 200),
            Order.OrderStatus.DANG_CHE_BIEN, new Color(255, 200, 100),
            Order.OrderStatus.HOAN_THANH, new Color(200, 255, 200),
            Order.OrderStatus.DA_HUY, new Color(255, 200, 200)
    );

    public OrderPanel() {
        setLayout(new BorderLayout(10,10));
        initComponents();
        setupTableRenderers();
        loadOrders();
        refreshData();
    }

    private void initComponents() {
        searchField = new JTextField(20);
        statusFilter = new JComboBox<>(Order.OrderStatus.values());
        statusFilter.insertItemAt(null, 0);
        statusFilter.setSelectedItem(null);
        statsLabel = new JLabel();

        orderModel = new DefaultTableModel(new String[]{"ID", "Khách hàng", "Bàn", "Giờ đặt", "Tổng tiền", "Trạng thái"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isUserInteracting = true; // 🔥 user đang tương tác
                showOrderDetails();
            }
        });


        detailModel = new DefaultTableModel(new String[]{"Món", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        detailTable = new JTable(detailModel);

        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderDetails();
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tìm kiếm:"));
        top.add(searchField);
        top.add(new JLabel("Trạng thái:"));
        top.add(statusFilter);
        top.add(Box.createHorizontalStrut(20));
        top.add(statsLabel);

        searchField.addCaretListener(e -> filterOrders());
        statusFilter.addActionListener(e -> filterOrders());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(createButton("Xác nhận", this::handleConfirm));
        buttons.add(createButton("Chế biến", this::handleCooking));
        buttons.add(createButton("Hoàn thành", this::handleComplete));
        buttons.add(createButton("Hủy đơn", this::handleCancel));
        buttons.add(createButton("Làm mới", e -> loadOrders()));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(orderTable), new JScrollPane(detailTable));
        split.setResizeWeight(0.5);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }
    private void setupTableRenderers() {
        // Renderer riêng cho trạng thái
        orderTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && value instanceof Order.OrderStatus) {
                    Order.OrderStatus status = (Order.OrderStatus) value;
                    c.setBackground(STATUS_COLORS.getOrDefault(status, Color.WHITE));
                } else if (!isSelected) {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // 🔥 Làm đẹp bảng
        orderTable.setRowHeight(30);
        orderTable.setShowHorizontalLines(false);
        orderTable.setShowVerticalLines(false);
        orderTable.setIntercellSpacing(new Dimension(0, 0));
        orderTable.setFillsViewportHeight(true);
        orderTable.setBackground(Color.WHITE);
        orderTable.setSelectionBackground(new Color(230, 240, 255));
        orderTable.setSelectionForeground(Color.BLACK);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderTable.setOpaque(false);

        JTableHeader header = orderTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(new Color(100, 150, 255));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setOpaque(false);

        // 🔥 Custom scrollPane thật sự (chỉ lấy ancestor một lần duy nhất)
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, orderTable);
        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(new Color(250, 250, 255));
            scrollPane.setBackground(new Color(250, 250, 255));
            scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JPanel());
        }
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.addActionListener(listener);
        return btn;
    }

    private void loadOrders() {
        SwingWorker<List<Order>, Void> worker = new SwingWorker<>() {
            protected List<Order> doInBackground() {
                return OrderDAO.getInstance().selectAll();
            }
            protected void done() {
                try {
                    allOrders = get();
                    filterOrders();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void filterOrders() {
        orderModel.setRowCount(0);
        String search = searchField.getText().toLowerCase();
        Order.OrderStatus filter = (Order.OrderStatus) statusFilter.getSelectedItem();

        List<Order> filtered = allOrders.stream()
                .filter(o -> (search.isEmpty() || o.getCustomer().getUserName().toLowerCase().contains(search)) &&
                        (filter == null || o.getStatus() == filter))
                .collect(Collectors.toList());

        for (Order o : filtered) {
            orderModel.addRow(new Object[]{
                    o.getOrder_Id(),
                    o.getCustomer().getUserName(),
                    formatTable(o.getTable()),
                    formatter.format(o.getOrderDate()),
                    currencyFormat.format(calculateTotal(o)),
                    o.getStatus()
            });
        }

        statsLabel.setText(String.format("Tổng đơn: %d | Tổng tiền: %s",
                filtered.size(),
                currencyFormat.format(filtered.stream()
                        .map(this::calculateTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))));
    }

    private void showOrderDetails() {
        detailModel.setRowCount(0);
        int row = orderTable.getSelectedRow();
        if (row == -1) return;

        selectedOrderId = ((Integer) orderModel.getValueAt(row, 0)).longValue();
        allOrders.stream()
                .filter(o -> o.getOrder_Id() == selectedOrderId)
                .findFirst()
                .ifPresent(order -> {
                    for (OrderItem item : order.getOrderItems()) {
                        detailModel.addRow(new Object[]{
                                item.getMenuItem().getName(),
                                item.getQuantity(),
                                currencyFormat.format(item.getPrice()),
                                currencyFormat.format(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        });
                    }
                });
    }

    private BigDecimal calculateTotal(Order order) {
        return order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatTable(TableBooking table) {
        return table.getTableType() == TableBooking.TableType.PHONG_VIP ? "Phòng VIP - " + table.getTables_id() : "Bàn - " + table.getTables_id();
    }

    private void refreshData() {
        if (isUserInteracting) {
            System.out.println("⏸️ Đang thao tác => không refresh!");
            return; // Đang thao tác, không refresh
        }

        // Nếu rảnh thì mới refresh
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            protected Void doInBackground() {
                loadOrders();
                return null;
            }
        };
        worker.execute();
    }


    private void updateOrderStatus(Order.OrderStatus newStatus) {
        if (selectedOrderId == null) return;
        OrderDAO.getInstance().updateOrderStatus(selectedOrderId, newStatus);
        loadOrders();
        JOptionPane.showMessageDialog(this, "✅ Cập nhật trạng thái thành công!");
    }


    private void handleConfirm(ActionEvent e) {
        isUserInteracting = true;
        updateOrderStatus(Order.OrderStatus.DA_XAC_NHAN);
    }

    private void handleCooking(ActionEvent e) {
        updateOrderStatus(Order.OrderStatus.DANG_CHE_BIEN);
    }

    private void handleComplete(ActionEvent e) {
        updateOrderStatus(Order.OrderStatus.HOAN_THANH);
    }

    private void handleCancel(ActionEvent e) {
        updateOrderStatus(Order.OrderStatus.DA_HUY);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }
}
