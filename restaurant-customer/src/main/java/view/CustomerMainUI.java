package view;

import javax.swing.*;

public class CustomerMainUI extends JFrame {

    public CustomerMainUI(String username) {
        setTitle("Khách hàng - " + username);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("💬 Nhắn với nhà hàng", new ChatPanel(username));

        add(tabs);
    }
}
