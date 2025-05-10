package view;


import javax.swing.*;

public class StaffMainUI extends JFrame {

    public StaffMainUI() {
        setTitle("Nhà Hàng -5 sao");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("💬 Nhắn với khách hàng", new StaffChatPanel());

        add(tabs);
    }
}
