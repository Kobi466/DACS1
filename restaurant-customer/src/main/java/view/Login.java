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
    public JTextField loginUsernameField;
    public JPasswordField loginPasswordField;

    // Đăng ký
    public JTextField registerUsernameField;
    public JPasswordField registerPasswordField;
    public JTextField registerPhoneField;

    public Login() {
        applyTheme();
        initSocket();
        this.init();
        this.setVisible(true);
    }

    private void init() {
        setTitle("Đăng nhập / Đăng ký");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);

        // Sử dụng sự kiện từ controller
        ActionListener ac = new LoginController(this);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // ==================== Giao diện đăng nhập ====================
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;

        loginPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridy++;
        loginPanel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginUsernameField = new JTextField(15);
        loginPanel.add(loginUsernameField, gbc);

        gbc.gridy++;
        loginPasswordField = new JPasswordField(15);
        loginPanel.add(loginPasswordField, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.CENTER;

        JButton loginButton = createStyledButton("Đăng nhập", new Color(0x4CAF50), Color.WHITE);
        loginButton.addActionListener(ac);
        loginPanel.add(loginButton, gbc);

        tabbedPane.add("Đăng nhập", loginPanel);

        // ==================== Giao diện đăng ký ====================
        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

// Reset GridBagConstraints trước khi sử dụng
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;

// Label cho "Tên đăng ký"
        registerPanel.add(new JLabel("Tên đăng ký:"), gbc);

// TextField cho "Tên đăng ký"
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerUsernameField = new JTextField(15);
        registerPanel.add(registerUsernameField, gbc);

// Label cho "Mật khẩu"
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        registerPanel.add(new JLabel("Mật khẩu:"), gbc);

// PasswordField cho "Mật khẩu"
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerPasswordField = new JPasswordField(15);
        registerPanel.add(registerPasswordField, gbc);

// Label cho "Số điện thoại"
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        registerPanel.add(new JLabel("Số điện thoại:"), gbc);

// TextField cho "Số điện thoại"
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerPhoneField = new JTextField(15);
        registerPanel.add(registerPhoneField, gbc);

// Nút "Đăng ký"
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.CENTER;

        JButton registerButton = createStyledButton("Đăng ký", new Color(0x2196F3), Color.WHITE);
        registerButton.addActionListener(ac);
        registerPanel.add(registerButton, gbc);

        tabbedPane.add("Đăng ký", registerPanel);

        // ==================== Tổng quan giao diện ====================
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void applyTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSocket() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    SocketClient.connect("localhost", 8080);
                    System.out.println("✅ Kết nối socket thành công!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Login.this, "Không thể kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatLightLaf());
            new Login().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}