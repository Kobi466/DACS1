package view;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class AppRestaurant {
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatDarkLaf());
            new StaffMainUI().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
