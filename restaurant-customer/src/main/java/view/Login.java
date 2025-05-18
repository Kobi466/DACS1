package view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import controller.LoginController;
import socket.SocketClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JTabbedPane tabbedPane;

    // Đăng nhập
    public static JTextField loginUsernameField;
    public static JPasswordField loginPasswordField;

    // Đăng ký
    public static JTextField registerUsernameField;
    public static JPasswordField registerPasswordField;
    public static JTextField registerPhoneField;
    public Login(){
        this.init();
        SocketClient.connect("localhost", 8080);
        this.setVisible(true);
    }
    private void init() {
        setTitle("Đăng nhập / Đăng ký");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        ActionListener ac = new LoginController(this);

        tabbedPane = new JTabbedPane();

        // Giao diện đăng nhập
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        loginUsernameField = new JTextField();
        loginPasswordField = new JPasswordField();
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.addActionListener(ac);

        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loginPanel.add(new JLabel("Tên đăng nhập:"));
        loginPanel.add(loginUsernameField);
        loginPanel.add(new JLabel("Mật khẩu:"));
        loginPanel.add(loginPasswordField);
        loginPanel.add(new JLabel(""));
        loginPanel.add(loginButton);

        // Giao diện đăng ký
        JPanel registerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        registerUsernameField = new JTextField();
        registerPasswordField = new JPasswordField();
        registerPhoneField = new JTextField();
        JButton registerButton = new JButton("Đăng ký");
        registerButton.addActionListener(ac);

        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        registerPanel.add(new JLabel("Tên đăng ký:"));
        registerPanel.add(registerUsernameField);
        registerPanel.add(new JLabel("Mật khẩu:"));
        registerPanel.add(registerPasswordField);
        registerPanel.add(new JLabel("Số điện thoại:"));
        registerPanel.add(registerPhoneField);
        registerPanel.add(new JLabel(""));
        registerPanel.add(registerButton);

        tabbedPane.add("Đăng nhập", loginPanel);
        tabbedPane.add("Đăng ký", registerPanel);

        add(tabbedPane);

        // Sự kiện đăng nhập


        // Sự kiện đăng ký

    }

    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatLightLaf());
            new Login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
