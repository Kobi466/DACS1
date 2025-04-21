package view.restaurantView;

import com.formdev.flatlaf.FlatLightLaf;
import view.customerView.LoginUI;

import javax.swing.*;

public class StaffGUI extends JFrame {
    public StaffGUI() {
        setTitle("Nhân viên - Quản lý nhà hàng");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Chat", new StaffChatPanel());
        // tabs.addTab("Đơn hàng", new OrderPanel()); // có thể thêm sau
        // tabs.addTab("Bếp", new KitchenPanel());     // nếu cần

        add(tabs);
    }

    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatLightLaf());
        }catch (Exception e){
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() ->{
            new StaffGUI().setVisible(true);
        });
    }
}

