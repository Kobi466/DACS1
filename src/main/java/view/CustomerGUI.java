package view;// ✅ GUI khách hàng: đặt món + nhắn tin với thiết kế cải tiến đẹp hơn (CustomerGUI.java)

import jakarta.persistence.EntityManager;
import model.*;
import model.MenuItem;
import util.HibernateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CustomerGUI extends JFrame {
    private JTable menuTable;
    private JTextField quantityField;
    private JTextArea messageArea;
    private JButton orderButton;
    private JButton sendMessageButton;
    private Customer currentCustomer;
    private EntityManager em;

    public CustomerGUI(Customer customer) {
        this.currentCustomer = customer;
        this.em = HibernateUtil.getSessionFactory().createEntityManager();

        setTitle("Smart Restaurant - Khách hàng");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel title = new JLabel("🧾 Danh sách món ăn", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Menu Table
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Tên món", "Giá", "Còn lại"}, 0);
        menuTable = new JTable(tableModel);
        menuTable.setRowHeight(28);
        loadMenu(tableModel);

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Menu"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Order Panel
        JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel quantityLabel = new JLabel("Số lượng:");
        quantityField = new JTextField(4);
        orderButton = new JButton("Đặt món 🍽️");

        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderPanel.add(quantityLabel);
        orderPanel.add(quantityField);
        orderPanel.add(orderButton);

        // Message Panel
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBorder(BorderFactory.createTitledBorder("💬 Gửi tin nhắn cho nhà hàng"));
        messageArea = new JTextArea(4, 40);
        messageArea.setLineWrap(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane msgScroll = new JScrollPane(messageArea);
        sendMessageButton = new JButton("📨 Gửi tin nhắn");

        messagePanel.add(msgScroll, BorderLayout.CENTER);
        messagePanel.add(sendMessageButton, BorderLayout.SOUTH);

        bottomPanel.add(orderPanel, BorderLayout.NORTH);
        bottomPanel.add(messagePanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Events
        orderButton.addActionListener(this::handleOrder);
        sendMessageButton.addActionListener(this::handleMessage);
    }

    private void loadMenu(DefaultTableModel model) {
        em.getTransaction().begin();
        List<MenuItem> menu = em.createQuery("from MenuItem", MenuItem.class).getResultList();
        for (MenuItem item : menu) {
            model.addRow(new Object[]{item.getName(), item.getPrice(), item.getQuantity()});
        }
        em.getTransaction().commit();
    }

    private void handleOrder(ActionEvent e) {
        int row = menuTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn món ăn trước.", "⚠️ Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) throw new NumberFormatException();

            String name = (String) menuTable.getValueAt(row, 0);

            em.getTransaction().begin();
            MenuItem item = em.createQuery("from MenuItem where name = :name", MenuItem.class)
                    .setParameter("name", name)
                    .getSingleResult();

            Order order = new Order();
            order.setCustomer(currentCustomer);
            order.setOrderDate(java.time.LocalDateTime.now());
            order.setStatus(Order.OrderStatus.HoànThành);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(item);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(item.getPrice());

            order.setOrderItems(List.of(orderItem));

            em.persist(order);
            em.persist(orderItem);
            em.getTransaction().commit();

            JOptionPane.showMessageDialog(this, "Đặt món thành công! 🍽️");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.", "❌ Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleMessage(ActionEvent e) {
        String content = messageArea.getText().trim();
        if (content.isEmpty()) return;

        em.getTransaction().begin();
        Message msg = new Message();
        msg.setCustomer(currentCustomer);
        msg.setSender("Customer");
        msg.setContent(content);
        msg.setSent_at(java.time.LocalDateTime.now());
        em.persist(msg);
        em.getTransaction().commit();

        messageArea.setText("");
        JOptionPane.showMessageDialog(this, "Đã gửi tin nhắn. ✅");
    }

    public static void main(String[] args) {
        EntityManager em = HibernateUtil.getSessionFactory().createEntityManager();
        em.getTransaction().begin();
        Customer c = em.createQuery("from Customer", Customer.class).setMaxResults(1).getSingleResult();
        em.getTransaction().commit();
        new CustomerGUI(c).setVisible(true);
    }
}
