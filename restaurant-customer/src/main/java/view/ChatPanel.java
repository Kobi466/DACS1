package view;

import controller.MessageController;
import service.CustomerMessageService;
import util.MessageBubble;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class ChatPanel extends JPanel {

    private JPanel messageContainer; // Chứa toàn bộ tin nhắn
    private JScrollPane scrollPane; // Khung cuộn chứa tin nhắn
    public JTextField inputField; // Ô nhập nội dung
    private JButton sendButton; // Nút gửi tin nhắn
    private JLabel headerLabel; // Hiển thị tên phòng chat (giống Zalo)
    private CustomerMessageService messageService;
    public String currentUsername; // Tên người dùng hiện tại
    private static ChatPanel instance;
    private MessageController messageController;

    public ChatPanel(String currentUsername) {
        this.currentUsername = currentUsername;
        this.messageService = new CustomerMessageService();
        this.messageController = new MessageController(this, messageService);
        instance = this;
        initUI();
        this.messageController.loadChatHistory();
        CustomerMessageService.listenForMessages("localhost", 8080);
    }

    public static ChatPanel getInstance() {
        return instance;
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ===== Header (giống như tiêu đề ở Zalo) =====
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBackground(new Color(0x128C7E));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        headerLabel = new JLabel("Chat với Nhà Hàng");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        header.add(headerLabel, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // ===== Khung chứa tin nhắn =====
        messageContainer = new JPanel();
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
        messageContainer.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(messageContainer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // ===== Khung nhập tin nhắn =====
        JPanel inputArea = new JPanel(new BorderLayout(10, 0));
        inputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD1D1D1), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        inputField.setBackground(Color.WHITE);
        inputField.setOpaque(true); // Làm sáng background


        // ==== Nút Gửi (Send Button) dài hơn ====
        sendButton = new JButton("Gửi");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(0x25D366)); // Xanh lá (đặc trưng Zalo/WhatsApp)
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30)); // Kéo dài chiều ngang
        // ==== Hover effect cho nút Gửi ====
        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(new Color(0x1DA653)); // Màu xanh đậm hơn khi Hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(new Color(0x33B6FF)); // Trở lại màu gốc
            }
        });


        // ==== Nút "Cú pháp" với biểu tượng ====
        JButton suggestButton = new JButton("Cú pháp");
        suggestButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        suggestButton.setBackground(new Color(0x128C7E)); // Xanh đậm
        suggestButton.setForeground(Color.WHITE);
        suggestButton.setFocusPainted(false);
        suggestButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        suggestButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Kéo dài chiều ngang

        // ==== Hover effect cho nút Cú pháp ====
        suggestButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                suggestButton.setBackground(new Color(0x0A6E62)); // Màu xanh tối hơn khi Hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                suggestButton.setBackground(new Color(0x128C7E)); // Trở lại màu gốc
            }
        });

        // ==== Gắn sự kiện gợi ý vào nút Cú pháp ====
        suggestButton.addActionListener(e -> showSuggestions(suggestButton));


        inputArea.add(suggestButton, BorderLayout.WEST);
        inputArea.add(inputField, BorderLayout.CENTER);
        inputArea.add(sendButton, BorderLayout.EAST);

        add(inputArea, BorderLayout.SOUTH);

        // ===== Thiết lập sự kiện để gửi tin nhắn =====
        ActionListener ac = new MessageController(this, messageService);
        sendButton.addActionListener(ac);
        inputField.addActionListener(ac);
    }

    private void showSuggestions(JButton suggestButton) {
        JPopupMenu menu = new JPopupMenu();
        String[] suggestions = {
                "đặt bàn [ngày/tháng] lúc [hh:mm] [BAN1->8/PHONGVIP1->6] [món ăn] [số lượng] xuất",
                "Cho tôi xem menu quán",
                "Cho tôi xem những bàn trống hiện tại của nhà hàng"
        };
        for (String suggestion : suggestions) {
            JMenuItem menuItem = new JMenuItem(suggestion);
            menuItem.addActionListener(e -> inputField.setText(suggestion));
            menu.add(menuItem);
        }
        menu.show(suggestButton, 0, suggestButton.getHeight());
    }

    public void appendMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            // ===== Tin nhắn bong bóng được căn trái/phải =====
            boolean isSender = sender.equals(currentUsername);
            MessageBubble bubble = new MessageBubble(message, isSender);

            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
            messagePanel.setBackground(Color.WHITE);

            if (isSender) {
                messagePanel.add(Box.createHorizontalGlue());
                messagePanel.add(bubble);
            } else {
                messagePanel.add(bubble);
                messagePanel.add(Box.createHorizontalGlue());
            }

            messageContainer.add(messagePanel);
            messageContainer.add(Box.createVerticalStrut(10));

            messageContainer.revalidate();
            messageContainer.repaint();

            scrollToBottom();
        });
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }
}