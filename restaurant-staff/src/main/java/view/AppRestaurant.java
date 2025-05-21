package view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class AppRestaurant {
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatLightLaf());
            new StaffMainUI().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
