package view.customerView;

import dao.CustomerDAO;
import dao.MenuItemDAO;
import dao.TableBookingDAO;
import jakarta.persistence.EntityManager;
import model.*;
import model.MenuItem;
import util.HibernateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public class OrderPanel extends JPanel {
    // UI Components
    private JTable menuTable;
    private JTable cartTable;
    private JTextField quantityField;
    private JComboBox<String> tableComboBox;
    private JTextField bookingTimeField;
    private JLabel totalPriceLabel;

    // Data models
    private DefaultTableModel menuModel;
    private DefaultTableModel cartModel;
    private Map<MenuItem, Integer> cartItems;
    private List<MenuItem> menuItems;
    private List<TableBooking> availableTables;
    private Customer currentCustomer;

    public OrderPanel() {
        String username = LoginUI.usernameField.getText();
        String password = LoginUI.passwordField.getText();
        this.currentCustomer = CustomerDAO.getInstance().findByUsername(username, password);
        this.cartItems = new HashMap<>();

        initializeData();
        setupUI();
    }

    private void initializeData() {
        menuItems = new MenuItemDAO().findAvailable();
        availableTables = new TableBookingDAO().getAvailableTables();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Tạo split pane chính
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setResizeWeight(0.6);

        // Panel menu phía trên
        JPanel topPanel = createMenuPanel();
        mainSplitPane.setTopComponent(topPanel);

        // Panel giỏ hàng phía dưới
        JPanel bottomPanel = createCartPanel();
        mainSplitPane.setBottomComponent(bottomPanel);

        // Panel điều khiển phía dưới
        JPanel controlPanel = createControlPanel();

        add(mainSplitPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tạo model và table cho menu
        menuModel = new DefaultTableModel(
                new Object[]{"Tên món", "Giá", "Còn lại"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        menuTable = new JTable(menuModel);
        menuTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadMenuData();

        // Panel điều khiển menu
        JPanel menuControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityField = new JTextField(5);
        JButton addToCartButton = new JButton("Thêm vào giỏ");
        addToCartButton.addActionListener(this::handleAddToCart);

        menuControlPanel.add(new JLabel("Số lượng:"));
        menuControlPanel.add(quantityField);
        menuControlPanel.add(addToCartButton);

        panel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        panel.add(menuControlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tạo model và table cho giỏ hàng
        cartModel = new DefaultTableModel(
                new Object[]{"Tên món", "Số lượng", "Đơn giá", "Thành tiền"}, 0);
        cartTable = new JTable(cartModel);
        cartTable.setEnabled(false);

        // Panel hiển thị tổng tiền
        totalPriceLabel = new JLabel("Tổng tiền: 0 VNĐ");
        totalPriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        panel.add(totalPriceLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        tableComboBox = new JComboBox<>();
        for (TableBooking table : availableTables) {
            tableComboBox.addItem(table.toString());
        }

        bookingTimeField = new JTextField(15);
        bookingTimeField.setText(LocalDateTime.now().plusHours(1).toString());

        JButton confirmOrderButton = new JButton("Xác nhận đặt hàng");
        confirmOrderButton.addActionListener(this::handleConfirmOrder);

        panel.add(new JLabel("Chọn bàn:"));
        panel.add(tableComboBox);
        panel.add(new JLabel("Thời gian:"));
        panel.add(bookingTimeField);
        panel.add(confirmOrderButton);

        return panel;
    }

    private void loadMenuData() {
        menuModel.setRowCount(0);
        for (MenuItem item : menuItems) {
            menuModel.addRow(new Object[]{
                    item.getName(),
                    item.getPrice().toString(),
                    item.getQuantity()
            });
        }
    }

    private void updateCartTable() {
        cartModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<MenuItem, Integer> entry : cartItems.entrySet()) {
            MenuItem item = entry.getKey();
            int quantity = entry.getValue();
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(subtotal);

            cartModel.addRow(new Object[]{
                    item.getName(),
                    quantity,
                    item.getPrice(),
                    subtotal
            });
        }

        totalPriceLabel.setText("Tổng tiền: " + total + " VNĐ");
    }

    private void handleAddToCart(ActionEvent e) {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0.");
                return;
            }

            MenuItem selectedItem = menuItems.get(selectedRow);
            if (selectedItem.getQuantity() < quantity) {
                JOptionPane.showMessageDialog(this, "Không đủ món tồn kho.");
                return;
            }

            cartItems.merge(selectedItem, quantity, Integer::sum);
            updateCartTable();
            quantityField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.");
        }
    }

    private void handleConfirmOrder(ActionEvent e) {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            return;
        }

        try {
            LocalDateTime bookingTime = LocalDateTime.parse(bookingTimeField.getText());
            String tableName = (String) tableComboBox.getSelectedItem();
            if (tableName == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn.");
                return;
            }

            var em = HibernateUtil.getSessionFactory().createEntityManager();
            em.getTransaction().begin();

            // Tạo đơn hàng
            Order order = createOrder(em, bookingTime, tableName);

            // Lưu vào database
            em.persist(order);
            order.getOrderItems().forEach(em::persist);

            // Tạo đặt bàn
            createReservation(em, order, bookingTime);

            em.getTransaction().commit();
            em.close();

            // Reset UI
            resetAfterOrder();
            JOptionPane.showMessageDialog(this, "✅ Đặt hàng thành công!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi đặt hàng: " + ex.getMessage());
        }
    }

    private Order createOrder(EntityManager em, LocalDateTime bookingTime, String tableName) {
        Order order = new Order();
        order.setCustomer(em.merge(currentCustomer));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.CHO_XAC_NHAN);

        // Xử lý bàn
        TableBooking selectedTable = availableTables.stream()
                .filter(t -> t.toString().equals(tableName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));

        selectedTable.setStatus(TableBooking.StatusTable.DA_DAT);
        em.merge(selectedTable);
        order.setTable(selectedTable);

        // Xử lý các món
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<MenuItem, Integer> entry : cartItems.entrySet()) {
            MenuItem menuItem = em.merge(entry.getKey());

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(entry.getValue());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);

            menuItem.setQuantity(menuItem.getQuantity() - entry.getValue());
        }

        order.setOrderItems(orderItems);
        return order;
    }

    private void createReservation(EntityManager em, Order order, LocalDateTime bookingTime) {
        Reservation reservation = new Reservation();
        reservation.setCustomer(order.getCustomer());
        reservation.setTableBooking(order.getTable());
        reservation.setBookingTime(bookingTime);
        reservation.setStatus(Reservation.ReservationStatus.CHO_XAC_NHAN);
        em.persist(reservation);
    }

    private void resetAfterOrder() {
        cartItems.clear();
        updateCartTable();
        loadMenuData();
        bookingTimeField.setText(LocalDateTime.now().plusHours(1).toString());
    }
}