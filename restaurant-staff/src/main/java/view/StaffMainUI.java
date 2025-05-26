package view;


import network.GlobalResponseRouter;

import javax.swing.*;

public class StaffMainUI extends JFrame {

    public StaffMainUI() {
        GlobalResponseRouter.startListening("localhost", 8080);
        setTitle("Nhà Hàng -5 sao");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("💬 Nhắn với khách hàng", new StaffChatPanel());
        tabs.add("📦 Đơn hàng", new OrderPanel());
        tabs.add("🍽️ Bàn ăn", new TablePanel());


        add(tabs);
    }
}
