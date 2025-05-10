package view;

import dto.MessageDTO;
import service.CustomerMessageService;
import javax.swing.*;
import java.awt.*;


public class ChatPanel extends JPanel {

    private static JTextArea chatArea; // Khu vực hiển thị lịch sử chat
    private JTextField inputField; // Khu vực nhập tin nhắn
    private JButton sendButton; // Nút gửi tin nhắn
    private CustomerMessageService messageService; // Service để giao tiếp với server
    private String currentUsername; // Username của customer
    private static ChatPanel instance;
    public ChatPanel(String currentUsername) {
        this.currentUsername = currentUsername;
        this.messageService = new CustomerMessageService();
        instance  = this; // Lưu instance để sử dụng trong RealTimeResponseHandler
        initUI();              // Tạo giao diện
        loadChatHistory();     // Lấy lịch sử tin nhắn từ server
        CustomerMessageService.listenForMessages("localhost", 8080); // Địa chỉ và cổng của server
    }


    public static ChatPanel getInstance() {
        return instance;
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
        sendButton.addActionListener(e -> {
            String content = inputField.getText().trim();
            if (!content.isEmpty()) {
                String toUsername = "staff"; // hoặc lấy từ lựa chọn user đang chat
                messageService.sendMessage(currentUsername, toUsername, content);
                inputField.setText("");
            }
        });

        inputField.addActionListener(e -> sendMessage()); // Nhấn Enter cũng có thể gửi
        inputField.addActionListener(e -> sendButton.doClick());

    }

    private void sendMessage() {
        String content = inputField.getText().trim();
        if (content.isEmpty()) return;

        String toUsername = "staff"; // Giả định là gửi tới nhân viên tên "staff"
        messageService.sendMessage(currentUsername, toUsername, content);

        appendMessage(currentUsername, content); // ✅ Thêm dòng này để hiển thị luôn

        inputField.setText("");
    }



    // Nhận tin nhắn từ nhân viên và hiển thị trên giao diện
    public void receiveMessage(MessageDTO message) {
        appendMessage("Nhân viên", message.getContent());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // Lấy lịch sử tin nhắn từ server (từ CustomerMessageService)
    private void loadChatHistory() {
        String toUsername = "staff"; // Cố định hoặc có thể chọn sau này
        messageService.getChatHistory(currentUsername, toUsername, messages -> {
            SwingUtilities.invokeLater(() -> {
                for (MessageDTO msg : messages) {
                    String displayText = msg.getSender() + ": " + msg.getContent() + "\n";
                    chatArea.append(displayText);
                }
            });
        });
    }


    // Thêm tin nhắn mới vào khu vực hiển thị chat
    public void appendMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(sender + ": " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

}