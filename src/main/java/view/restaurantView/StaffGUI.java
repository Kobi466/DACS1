package view.restaurantView;

import com.formdev.flatlaf.FlatLightLaf;
import view.restaurantView.kho.OrderManagementPanel;

import javax.swing.*;

public class StaffGUI extends JFrame {
    public StaffGUI() {
        setTitle("Nhân viên - Quản lý nhà hàng");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Chat", new StaffChatPanel());
        tabs.addTab("Đơn hàng", new OrderPanel());
        tabs.addTab("Bàn", new ReservationManagementPanel());
//        tabs.addTab("Bếp", new KitchenPanel());
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

