package view.customerView;


import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class CustomerGUI extends JFrame {

    public CustomerGUI() {
        setTitle("Customer - Restaurant App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("🥘 Đặt món", new OrderPanel());
        tabbedPane.addTab("💬 Chat", new CustomerChatPanel());

        add(tabbedPane);
        setVisible(true);
    }
}
