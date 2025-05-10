package view;


import com.formdev.flatlaf.*;
import dto.CustomerDTO;
import network.JsonResponse;
import service.LoginService;
import session.SessionManager;
import socket.SocketClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class LoginUI extends JFrame {
    public static JTextField usernameField;
    public static JPasswordField passwordField;
    public JButton loginButton;
    public JPanel panel;
    public JLabel titleLabel, usernameLabel, passwordLabel;
    public JButton exitButton;
    public JButton registerButton;
    public JPanel panelImg;
    public int yOffset = 69;
    public JPanel sidebar;
    public boolean sidebarVisible = false;
    public int sidebarWidth = 300;
    public int animationSpeed = 10;
    public ArrayList<String> listPass;
    public JPasswordField resetpass;
    public JButton xacNhan;
    public JButton hienThiPass;
    public boolean[] hienMatKhau = new boolean[]{false};
    public LoginUI(){
        this.init();
        this.SideBar();
        this.Background();
        listPass = new ArrayList<>();
        add(panel);
        SocketClient.connect("localhost", 8080);
// Bắt đầu lắng nghe các phản hồi từ server
//        CustomerMessageService messageService = new CustomerMessageService();
//        messageService.listenForMessages("localhost", 8080);
        this.setVisible(true);
    }

    public void init() {
        setUndecorated(true);
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        String imagePath = "src/image/Logo.png";
        ImageIcon icon = new ImageIcon(imagePath);
        setIconImage(icon.getImage());

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("<html><i>Đăng nhập</i></html>", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 255, 255, 255));
        titleLabel.setBounds(50, 100, 200, 40);
        panel.add(titleLabel);

        usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(250, 250, 250, 255));
        usernameLabel.setBounds(50, 60 + yOffset, 200, 20);
        panel.add(usernameLabel);

        usernameField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                // Vẽ nền tùy chỉnh
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Vẽ viền bo góc
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        usernameField.setBounds(50, 80 + yOffset, 200, 35);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBackground(Color.WHITE);
        usernameField.setForeground(Color.BLACK);
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        usernameField.setOpaque(false);
        panel.add(usernameField);

        passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(255, 255, 255, 255));
        passwordLabel.setBounds(50, 120 + yOffset, 200, 20);
        panel.add(passwordLabel);

        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                // Vẽ nền tùy chỉnh
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Vẽ viền bo góc
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        passwordField.setBounds(50, 140 + yOffset, 200, 35);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(Color.BLACK);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        passwordField.setOpaque(false);
        panel.add(passwordField);

        loginButton = new JButton("Đăng nhập") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 236, 248));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        loginButton.setBounds(50, 190 + yOffset, 200, 40);
        loginButton.setForeground(new Color(18, 77, 228, 255));
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(false);
        loginButton.addActionListener(e -> {
            try {
                handleLogin();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        panel.add(loginButton);

        // Thêm nút Exit dưới nút đăng nhập
        exitButton = new JButton("<html><u>Thoát</u></html>");
        exitButton.setBounds(50, 235 + yOffset, 180, 25);
        exitButton.setForeground(new Color(255, 255, 255, 255));
        exitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        exitButton.setContentAreaFilled(false);
        exitButton.setOpaque(false);
        exitButton.addActionListener(e -> System.exit(0));
        panel.add(exitButton);
        animateWindow();
    }

    public void SideBar(){
        registerButton = new JButton("<html><u>Quên mật khẩu</u></html>");
        registerButton.setBounds(50, 260 + yOffset, 180, 25);
        registerButton.setForeground(new Color(255, 255, 255, 255));
        registerButton.setFont(new Font("Arial", Font.PLAIN, 16));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        registerButton.setContentAreaFilled(false);
        registerButton.setOpaque(false);
//        registerButton.addActionListener(ac);
        // Sidebar panel
        sidebar = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int arc = 25; // Độ cong của góc

                // Vẽ nền bo góc
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                // Vẽ viền bo góc
                g2.setColor(Color.WHITE); // Màu viền
                g2.setStroke(new BasicStroke(5)); // Độ dày viền
                g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

                g2.dispose();
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setOpaque(false); // Đảm bảo JPanel không đè nền vuông
        sidebar.setBackground(new Color(63, 73, 94));
        sidebar.setBounds(getWidth(), 0, sidebarWidth, getHeight());
        sidebar.setLayout(null);
        xacNhan = new JButton("OK"){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 236, 248));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };

        xacNhan.setBounds(110, 265, 80, 20);
        xacNhan.setForeground(new Color(18, 77, 228, 255));
        xacNhan.setFont(new Font("Arial", Font.BOLD, 16));
        xacNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        xacNhan.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        xacNhan.setContentAreaFilled(false);
        xacNhan.setOpaque(false);
//        xacNhan.addActionListener(ac);
        resetpass = new JPasswordField(){
            @Override
            protected void paintComponent(Graphics g) {
                // Vẽ nền tùy chỉnh
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Vẽ viền bo góc
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        resetpass.setBounds(50, 215, 200, 35);
        resetpass.setFont(new Font("Arial", Font.PLAIN, 16));
        resetpass.setBackground(Color.WHITE);
        resetpass.setForeground(Color.BLACK);
        resetpass.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        resetpass.setOpaque(false);
        sidebar.add(resetpass);
        sidebar.add(xacNhan);

        ImageIcon icon = new ImageIcon("C:\\Users\\ADMIN\\Pictures\\imageproject\\Logo.png");
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(170, 170, Image.SCALE_SMOOTH); // Resize ảnh về 80x40
        ImageIcon resizedIcon = new ImageIcon(newImg);
        JLabel imageLabel = new JLabel(resizedIcon);
        imageLabel.setBounds(65, 12, 170, 170);
        sidebar.add(imageLabel);
        JLabel passreset = new JLabel("Nhập mật khẩu mới:");
        passreset.setFont(new Font("Arial", Font.PLAIN, 14));
        passreset.setForeground(new Color(209, 226, 243, 255));
        passreset.setBounds(50, 195, 200, 20);
        sidebar.add(passreset);
        hienThiPass = new JButton("👁"){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 194, 253));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        hienThiPass.setForeground(new Color(255, 255, 255, 255));
        hienThiPass.setFont(new Font("Arial", Font.BOLD, 16));
        hienThiPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hienThiPass.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        hienThiPass.setContentAreaFilled(false);
        hienThiPass.setOpaque(false);
        hienThiPass.setBounds(255, 223, 20, 20);
//        hienThiPass.addActionListener(ac);
        sidebar.add(hienThiPass);
        panel.add(registerButton);
        this.add(sidebar);
    }

    public void Background(){
        panelImg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
//                ImageIcon icon = new ImageIcon(getClass().getResource(""));
                ImageIcon icon = new ImageIcon("C:\\Users\\ADMIN\\Pictures\\imageproject\\login.jpg");
                Image img = icon.getImage(); // Lấy ảnh gốc
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this); // Vẽ ảnh co giãn theo panel
            }
        };
        panelImg.setLayout(null);
        panelImg.setBounds(0, 0, 600, 390); // Kích thước Panel phù hợp với JFrame
        panel.add(panelImg);
    }

    public void toggleSidebar() {
        Timer timer = new Timer(10, new ActionListener() {
            private int step = sidebarVisible ? animationSpeed : -animationSpeed;
            private int targetX = sidebarVisible ? getWidth() : getWidth() - sidebarWidth;

            @Override
            public void actionPerformed(ActionEvent e) {
                int currentX = sidebar.getX();
                if ((sidebarVisible && currentX >= getWidth()) || (!sidebarVisible && currentX <= targetX)) {
                    ((Timer) e.getSource()).stop();
                    sidebarVisible = !sidebarVisible;
                    return;
                }
                sidebar.setBounds(currentX + step, 0, sidebarWidth, getHeight());
                repaint();
            }
        });
        timer.start();
    }

    private void animateWindow() {
        setOpacity(0f); // Bắt đầu với độ mờ hoàn toàn
        Timer timer = new Timer(25, new ActionListener() { // Chậm hơn chút
            private float opacity = 0f;
            private int yPosition = yOffset;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.03f; // Giảm tốc độ tăng độ mờ
                yPosition -= 1; // Giảm tốc độ di chuyển

                if (opacity > 1f) opacity = 1f;
                if (yPosition <= 0) {
                    yPosition = 0;
                    ((Timer) e.getSource()).stop();
                }

                setOpacity(opacity);
                for (Component comp : panel.getComponents()) {
                    comp.setLocation(comp.getX(), comp.getY() - 1); // Di chuyển tất cả các thành phần, bao gồm cả tiêu đề
                }
            }
        });
        timer.start();
    }
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        JsonResponse response = LoginService.login(username, password);

        if (response != null && "LOGIN_SUCCESS".equals(response.getStatus())) {
            // Debug dữ liệu nhận được từ server
            System.out.println("🟢 Debug: Response data: " + response.getData());

            // Chuyển đổi phản hồi sang CustomerDTO
            CustomerDTO customer = (CustomerDTO) response.getData();

            if (customer != null) {
                int customerId = customer.getCustomerId();

                // Kiểm tra giá trị customerId
                if (customerId > 0) {
                    // Gán customerId vào SessionManager
                    SessionManager.instance().setCustomerId(customerId);
                    System.out.println("✅ Gán ID khách hàng: " + customerId);

                    JOptionPane.showMessageDialog(this, "Login successful!");

                    // Mở giao diện chính
                    new CustomerMainUI(username).setVisible(true);
                    this.dispose();
                } else {
                    System.err.println("❌ Dữ liệu customerId không hợp lệ.");
                    JOptionPane.showMessageDialog(this, "Login failed. Invalid customerId.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Login failed. Invalid response data.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Login failed. Please try again.");
        }
    }


    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatLightLaf());
        }catch (Exception e){
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() ->{
            new LoginUI().setVisible(true);
        });
    }
}
