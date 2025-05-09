package view;

import dto.MessageDTO;
import service.CustomerMessageService;
import session.SessionManager; // Thêm import cho SessionManager

import javax.swing.*;
import java.awt.*;


public class ChatPanel extends JPanel {

    private JTextArea chatArea; // Khu vực hiển thị lịch sử chat
    private JTextField inputField; // Khu vực nhập tin nhắn
    private JButton sendButton; // Nút gửi tin nhắn
    private CustomerMessageService messageService; // Service để giao tiếp với server
    private String currentUsername; // Username của customer
    public ChatPanel(String currentUsername) {
        this.currentUsername = currentUsername;
        this.messageService = new CustomerMessageService();
        initUI();              // Tạo giao diện
        loadChatHistory();     // Lấy lịch sử tin nhắn từ server
    }

    // Tạo giao diện
    private void initUI() {
        setLayout(new BorderLayout());

        // Khu vực hiển thị tin nhắn
        chatArea = new JTextArea();
        chatArea.setEditable(false); // Không cho chỉnh sửa nội dung chat
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Khu vực nhập tin nhắn
        inputField = new JTextField();
        sendButton = new JButton("Gửi");

        // Tạo panel nhập tin nhắn
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Sự kiện gửi tin nhắn khi nhấn nút "Gửi"
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage()); // Nhấn Enter cũng có thể gửi
        inputField.addActionListener(e -> sendButton.doClick());

    }

    private void sendMessage() {
        String content = inputField.getText().trim();
        if (content.isEmpty()) return; // Không gửi khi tin nhắn trống

        // Check if user is logged in
        if (!SessionManager.instance().isLoggedIn()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng đăng nhập trước khi gửi tin nhắn!",
                    "Lỗi phiên làm việc",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Correct the method call by removing `customerId`
        messageService.sendMessage(currentUsername, "staff", content);

        // Hiển thị tin nhắn trong giao diện
        appendMessage("Bạn", content);
        inputField.setText(""); // Xóa nội dung sau khi gửi
    }

    // Nhận tin nhắn từ nhân viên và hiển thị trên giao diện
    public void receiveMessage(MessageDTO message) {
        appendMessage("Nhân viên", message.getContent());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // Lấy lịch sử tin nhắn từ server (từ CustomerMessageService)
    private void loadChatHistory() {
        int customerId = SessionManager.instance().getCustomerId();
        if (customerId <= 0) {
            System.err.println("❌ [Client] Không thể lấy lịch sử chat, customerId không hợp lệ");
            return;
        }

        System.out.println("🟢 Debug: Lấy lịch sử chat giữa " + currentUsername + " và staff.");
        messageService.getChatHistory(currentUsername, "staff", messages -> {
            for (MessageDTO message : messages) {
                String sender = message.getSender().equals(currentUsername) ? "Bạn" : "Nhân viên";
                appendMessage(sender, message.getContent());
            }
        });
    }

    // Thêm tin nhắn mới vào khu vực hiển thị chat
    private void appendMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n");
    }
}