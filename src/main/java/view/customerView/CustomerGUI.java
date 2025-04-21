package view.customerView;

// src/main/java/view/CustomerGUI.java


import javax.swing.*;

public class CustomerGUI extends JFrame {

    public CustomerGUI() {
        setTitle("Customer - Restaurant App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

//        tabbedPane.addTab("🥘 Đặt món", new OrderPanel());
//        tabbedPane.addTab("📅 Đặt bàn", new ReservationPanel());
        tabbedPane.addTab("💬 Chat", new CustomerChatPanel());
//        tabbedPane.addTab("📋 Đơn hàng", new OrderPanel());

        add(tabbedPane);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerGUI::new);
    }
}
