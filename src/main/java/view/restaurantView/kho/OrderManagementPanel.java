package view.restaurantView.kho;

import dao.MenuItemDAO;
import dao.OrderDAO;
import dao.TableBookingDAO;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.TableBooking;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderManagementPanel extends JPanel {
    private JTable orderTable;
    private JTable orderDetailTable;
    private DefaultTableModel orderModel;
    private DefaultTableModel detailModel;
    private final DateTimeFormatter dateFormatter;
    private JTextField searchField;
    private JComboBox<Order.OrderStatus> statusFilter;
    private JLabel statsLabel;
    private Order currentSelectedOrder = null;
    private Timer refreshTimer;
    private List<Order> currentOrders;
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private static final int REFRESH_INTERVAL = 10_000;
    private static final Map<Order.OrderStatus, Color> STATUS_COLORS = Map.of(
            Order.OrderStatus.CHO_XAC_NHAN, new Color(255, 255, 200),
            Order.OrderStatus.DA_XAC_NHAN, new Color(200, 255, 200),
            Order.OrderStatus.DANG_CHE_BIEN, new Color(255, 200, 100),
            Order.OrderStatus.HOAN_THANH, new Color(200, 255, 200),
            Order.OrderStatus.DA_HUY, new Color(255, 200, 200)
    );
    private Map<Integer, List<Order>> tableOrders; // Lưu trữ đơn theo bàn
    private JComboBox<String> tableFilter; // Bộ lọc theo bàn


    public OrderManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        currentOrders = new ArrayList<>();

        initializeComponents();
        setupLayout();
        loadOrders();
        startAutoRefresh();
    }

    private void initializeComponents() {
        // Khởi tạo các điều khiển
        searchField = createSearchField();
        statusFilter = createStatusFilter();
        statsLabel = new JLabel("Tổng đơn: 0 | Tổng tiền: 0đ");
        // Thêm bộ lọc bàn
        tableFilter = createTableFilter();

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Bàn:"));
        filterPanel.add(tableFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(statusFilter);

        // Khởi tạo bảng
        setupTables();

        // Thêm người nghe sự kiện
        setupEventListeners();
    }

    private JTextField createSearchField() {
        JTextField field = new JTextField(20);
        field.putClientProperty("JTextField.placeholderText", "Tìm kiếm khách hàng...");
        return field;
    }

    private JComboBox<Order.OrderStatus> createStatusFilter() {
        JComboBox<Order.OrderStatus> filter = new JComboBox<>(Order.OrderStatus.values());
        filter.insertItemAt(null, 0);
        filter.setSelectedItem(null);
        filter.setRenderer(new StatusComboRenderer());
        return filter;
    }

    private void setupTables() {
        // Mô hình bảng cho đơn hàng
        orderModel = new DefaultTableModel(
                new String[]{"Mã KH", "Khách hàng", "Số đơn", "Tổng món", "Tổng tiền", "Trạng thái", "Ghi chú"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Chỉ cho phép sửa cột ghi chú
            }
        };

        orderTable = new JTable(orderModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setRowHeight(25);

        // Mô hình bảng cho chi tiết
        detailModel = new DefaultTableModel(
                new String[]{"Mã món", "Tên món", "Số lượng", "Đơn giá", "Thành tiền", "Bàn", "Thời gian", "Ghi chú"},
                0
        );

        orderDetailTable = new JTable(detailModel);
        orderDetailTable.setRowHeight(25);

        // Thiết lập trình hiển thị
        setupTableRenderers();
    }
    private JComboBox<String> createTableFilter() {
        JComboBox<String> filter = new JComboBox<>();
        filter.addItem("Tất cả bàn");
        // Thêm danh sách bàn từ cơ sở dữ liệu
        List<TableBooking> tables = TableBookingDAO.getInstance().selectAll();
        for (TableBooking table : tables) {
            filter.addItem(formatTableInfo(table));
        }
        filter.addActionListener(e -> applyFilters());
        return filter;
    }



    private void setupTableRenderers() {
        // Trình hiển thị tiền tệ
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof BigDecimal) {
                    value = CURRENCY_FORMAT.format(((BigDecimal) value)) + " đ";
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        moneyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        // Trình hiển thị trạng thái
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Order.OrderStatus && !isSelected) {
                    setBackground(STATUS_COLORS.getOrDefault((Order.OrderStatus)value, Color.WHITE));
                }
                return c;
            }
        };

        // Áp dụng trình hiển thị
        orderTable.getColumn("Tổng tiền").setCellRenderer(moneyRenderer);
        orderTable.getColumn("Trạng thái").setCellRenderer(statusRenderer);
    }

    private void setupLayout() {
        // Panel phía trên với tìm kiếm và bộ lọc
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(statusFilter);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.add(statsLabel);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.EAST);

        // Nội dung chính
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(createTablePanel(orderTable, "Danh sách đơn hàng"));
        splitPane.setBottomComponent(createTablePanel(orderDetailTable, "Chi tiết đơn hàng"));
        splitPane.setResizeWeight(0.5);

        // Panel nút
        JPanel buttonPanel = createButtonPanel();

        // Lắp ráp bố cục
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }



    private JPanel createTablePanel(JTable table, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(table));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        // Tạo các nút với biểu tượng
        panel.add(createButton("Làm mới", "refresh", e -> refreshData()));
        panel.add(createButton("Xác nhận", "confirm", e -> handleUpdateStatus(Order.OrderStatus.DA_XAC_NHAN)));
        panel.add(createButton("Chế biến", "cooking", e -> handleUpdateStatus(Order.OrderStatus.DANG_CHE_BIEN)));
        panel.add(createButton("Hoàn thành", "complete", e -> handleUpdateStatus(Order.OrderStatus.HOAN_THANH)));
        panel.add(createButton("Hủy đơn", "cancel", e -> handleCancelOrder()));
        panel.add(createButton("In hóa đơn", "print", e -> handlePrintOrder()));

        return panel;
    }

    private JButton createButton(String text, String iconName, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        try {
            button.setIcon(new ImageIcon(getClass().getResource("/icons/" + iconName + ".png")));
        } catch (Exception e) {
            System.err.println("Không tìm thấy biểu tượng: " + iconName);
        }
        button.addActionListener(listener);
        button.setFocusPainted(false);
        return button;
    }

    private void loadOrders() {
        try {
            currentOrders = OrderDAO.getInstance().selectAll();
            updateOrdersTable();
            updateStatistics();
        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu đơn hàng", e);
        }
    }

    private void updateOrdersTable() {
        orderModel.setRowCount(0);
        String searchText = searchField.getText().toLowerCase();
        Order.OrderStatus selectedStatus = (Order.OrderStatus) statusFilter.getSelectedItem();

        Map<String, List<Order>> groupedOrders = currentOrders.stream()
                .filter(order -> filterOrder(order, searchText, selectedStatus))
                .collect(Collectors.groupingBy(order -> order.getCustomer().getUserName()));

        for (Map.Entry<String, List<Order>> entry : groupedOrders.entrySet()) {
            addOrderRow(entry.getValue());
        }
    }

    private boolean filterOrder(Order order, String searchText, Order.OrderStatus status) {
        boolean matchesSearch = searchText.isEmpty() ||
                order.getCustomer().getUserName().toLowerCase().contains(searchText);
        boolean matchesStatus = status == null || order.getStatus() == status;
        return matchesSearch && matchesStatus;
    }

    private void addOrderRow(List<Order> userOrders) {
        Order firstOrder = userOrders.get(0);
        orderModel.addRow(new Object[]{
                firstOrder.getCustomer().getCustomer_Id(),
                firstOrder.getCustomer().getUserName(),
                userOrders.size(),
                getTotalItems(userOrders),
                calculateTotalAmount(userOrders),
                getCommonStatus(userOrders),
                getOrderNotes(userOrders)
        });
    }

    private void showOrderDetails() {
        detailModel.setRowCount(0);
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) return;

        int customerId = (int) orderModel.getValueAt(selectedRow, 0);
        List<Order> userOrders = currentOrders.stream()
                .filter(o -> o.getCustomer().getCustomer_Id() == customerId)
                .collect(Collectors.toList());

        for (Order order : userOrders) {
            addOrderDetails(order);
        }
    }

    private void addOrderDetails(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            detailModel.addRow(new Object[]{
                    item.getMenuItem().getFood_Id(),
                    item.getMenuItem().getName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                    formatTableInfo(order.getTable()),
                    dateFormatter.format(order.getOrderDate()),
                    item.getKitchenQueue() != null ? item.getKitchenQueue().getOrderItem() : ""
            });
        }
    }

    private String formatTableInfo(TableBooking table) {
        return String.format("%s - %d",
                table.getTableType() == TableBooking.TableType.PHONG_VIP ? "Phòng VIP" : "Bàn",
                table.getTables_id());
    }

    private void updateStatistics() {
        int totalOrders = currentOrders.size();
        BigDecimal totalAmount = currentOrders.stream()
                .map(this::calculateOrderTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        statsLabel.setText(String.format("Tổng đơn: %d | Tổng tiền: %s",
                totalOrders, CURRENCY_FORMAT.format(totalAmount)));
    }

    private void applyFilters() {
        updateOrdersTable();
        updateStatistics();
    }

    private void handleUpdateStatus(Order.OrderStatus newStatus) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn đơn hàng cần cập nhật!");
            return;
        }

        String username = (String) orderModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cập nhật trạng thái tất cả đơn hàng của " + username + " thành " + getStatusText(newStatus) + "?",
                "Xác nhận cập nhật",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            updateOrdersStatus(selectedRow, newStatus);
        }
    }

    private void handleCancelOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn đơn hàng cần hủy!");
            return;
        }

        String username = (String) orderModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy tất cả đơn hàng của " + username + "?",
                "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            updateOrdersStatus(selectedRow, Order.OrderStatus.DA_HUY);
            restoreInventory(selectedRow);
        }
    }

    private void handlePrintOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn đơn hàng cần in!");
            return;
        }

        try {
            int customerId = (int) orderModel.getValueAt(selectedRow, 0);
            List<Order> ordersToPrint = currentOrders.stream()
                    .filter(o -> o.getCustomer().getCustomer_Id() == customerId)
                    .collect(Collectors.toList());

            printOrders(ordersToPrint);
            showInfo("Đã in hóa đơn thành công!");
        } catch (Exception e) {
            showError("Lỗi khi in hóa đơn", e);
        }
    }

    private void printOrders(List<Order> orders) {
        // Triển khai chức năng in ở đây
        // Có thể sử dụng JasperReports hoặc iText
    }

    private void updateOrdersStatus(int selectedRow, Order.OrderStatus newStatus) {
        try {
            int customerId = (int) orderModel.getValueAt(selectedRow, 0);
            List<Order> ordersToUpdate = currentOrders.stream()
                    .filter(o -> o.getCustomer().getCustomer_Id() == customerId)
                    .collect(Collectors.toList());

            for (Order order : ordersToUpdate) {
                OrderDAO.getInstance().updateOrderStatus((long) order.getOrder_Id(), newStatus);
            }

            refreshData();
            showInfo("Đã cập nhật trạng thái thành công!");
        } catch (Exception e) {
            showError("Lỗi khi cập nhật trạng thái", e);
        }
    }

    private void restoreInventory(int selectedRow) {
        try {
            int customerId = (int) orderModel.getValueAt(selectedRow, 0);
            List<Order> ordersToRestore = currentOrders.stream()
                    .filter(o -> o.getCustomer().getCustomer_Id() == customerId)
                    .collect(Collectors.toList());

            for (Order order : ordersToRestore) {
                for (OrderItem item : order.getOrderItems()) {
                    MenuItem menuItem = item.getMenuItem();
                    menuItem.setQuantity(menuItem.getQuantity() + item.getQuantity());
                    MenuItemDAO.getInstance().update(menuItem);
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi khôi phục tồn kho", e);
        }
    }

    private void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // Lưu đơn hàng đang chọn
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    int customerId = (int) orderTable.getValueAt(selectedRow, 0);
                    String username = (String) orderTable.getValueAt(selectedRow, 1);
                    // Lưu thông tin chi tiết để khôi phục
                    currentSelectedOrder = currentOrders.stream()
                            .filter(o -> o.getCustomer().getCustomer_Id() == customerId
                                    && o.getCustomer().getUserName().equals(username))
                            .findFirst()
                            .orElse(null);
                }

                loadOrders();
                return null;
            }

            @Override
            protected void done() {
                // Khôi phục lựa chọn sau khi làm mới
                if (currentSelectedOrder != null) {
                    for (int i = 0; i < orderTable.getRowCount(); i++) {
                        int customerId = (int) orderTable.getValueAt(i, 0);
                        String username = (String) orderTable.getValueAt(i, 1);

                        if (customerId == currentSelectedOrder.getCustomer().getCustomer_Id()
                                && username.equals(currentSelectedOrder.getCustomer().getUserName())) {
                            orderTable.setRowSelectionInterval(i, i);
                            Rectangle rect = orderTable.getCellRect(i, 0, true);
                            orderTable.scrollRectToVisible(rect);
                            break;
                        }
                    }
                }
                checkNewOrders();
            }
        };
        worker.execute();
    }

    private void checkNewOrders() {
        int newOrdersCount = OrderDAO.getInstance().findNewOrders().size();
        if (newOrdersCount > 0) {
            showNewOrderNotification(newOrdersCount);
        }
    }

    private void showNewOrderNotification(int count) {
        SwingUtilities.invokeLater(() -> {
            if (SystemTray.isSupported()) {
                displayTrayNotification("Đơn hàng mới",
                        "Có " + count + " đơn hàng mới cần xử lý!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "🔔 Có " + count + " đơn hàng mới cần xử lý!",
                        "Thông báo đơn mới",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void displayTrayNotification(String title, String message) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = new ImageIcon(getClass().getResource("/icons/notification.png")).getImage();
            TrayIcon trayIcon = new TrayIcon(image, "Restaurant Management");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Các phương thức trợ giúp
    private BigDecimal calculateOrderTotal(Order order) {
        return order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalAmount(List<Order> orders) {
        return orders.stream()
                .map(this::calculateOrderTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int getTotalItems(List<Order> orders) {
        return orders.stream()
                .mapToInt(order -> order.getOrderItems().size())
                .sum();
    }

    private Order.OrderStatus getCommonStatus(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Order.OrderStatus.CHO_XAC_NHAN);
    }

    private String getOrderNotes(List<Order> orders) {
        return orders.stream()
                .map(order -> order.getOrderItems().stream()
                        .filter(item -> item.getKitchenQueue() != null)
                        .map(OrderItem::getMenuItem)
                        .map(MenuItem::getName)
                        .collect(Collectors.joining("; ")))
                .filter(note -> !note.isEmpty())
                .collect(Collectors.joining(" | "));
    }

    private String getStatusText(Order.OrderStatus status) {
        switch (status) {
            case CHO_XAC_NHAN: return "Chờ xác nhận";
            case DA_XAC_NHAN: return "Đã xác nhận";
            case DANG_CHE_BIEN: return "Đang chế biến";
            case HOAN_THANH: return "Hoàn thành";
            case DA_HUY: return "Đã hủy";
            default: return status.toString();
        }
    }

    private void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(this,
                message + ": " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }

    // Trình hiển thị tùy chỉnh cho hộp combo trạng thái
    private class StatusComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Order.OrderStatus) {
                setText(getStatusText((Order.OrderStatus) value));
            } else if (value == null) {
                setText("Tất cả trạng thái");
            }
            return this;
        }
    }

    public void stopAutoRefresh() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }

    public void startAutoRefresh() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
        refreshTimer = new Timer(REFRESH_INTERVAL, e -> refreshData());
        refreshTimer.start();
    }

    private void setupEventListeners() {
        // Người nghe sự kiện lựa chọn đơn hàng
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Tạm dừng tự động làm mới khi người dùng đang xem chi tiết
                stopAutoRefresh();
                showOrderDetails();
                // Khởi động lại tự động làm mới sau 1 phút không tương tác
                Timer delayedRefreshTimer = new Timer(60000, ev -> startAutoRefresh());
                delayedRefreshTimer.setRepeats(false);
                delayedRefreshTimer.start();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new OrderManagementPanel());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}