package view;

import dto.MessageDTO;
import service.CustomerMessageService;
import util.RoundedTextArea;


import javax.swing.*;
import java.awt.*;


public class ChatPanel extends JPanel {

    private JPanel messageContainer; // Chứa toàn bộ tin nhắn
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;
    private CustomerMessageService messageService;
    private String currentUsername;
    private static ChatPanel instance;

    public ChatPanel(String currentUsername) {
        this.currentUsername = currentUsername;
        this.messageService = new CustomerMessageService();
        instance = this;

        initUI();
        loadChatHistory();
        CustomerMessageService.listenForMessages("localhost", 8080);
    }

    public static ChatPanel getInstance() {
        return instance;
    }



    private void initUI() {
        setLayout(new BorderLayout());

        messageContainer = new JPanel();
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
        messageContainer.setBackground(Color.WHITE);


        JScrollPane scrollPane = new JScrollPane(messageContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        sendButton = new JButton("Gửi");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);


        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }


    private void sendMessage() {
        String content = inputField.getText().trim();
        if (content.isEmpty()) return;

        String toUsername = "staff";
        messageService.sendMessage(currentUsername, toUsername, content);

        appendMessage(currentUsername, content); // Hiển thị luôn
        inputField.setText("");
    }

    public void appendMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            JPanel messagePanel = new JPanel(new FlowLayout(
                    sender.equals(currentUsername) ? FlowLayout.RIGHT : FlowLayout.LEFT
            ));
            messagePanel.setOpaque(false); // trong suốt

            Color bgColor = sender.equals(currentUsername) ? new Color(0xDCF8C6) : Color.WHITE;
            RoundedTextArea messageLabel = new RoundedTextArea(message, bgColor);


            // Màu nền và căn lề
            if (sender.equals(currentUsername)) {
                messageLabel.setBackground(new Color(0xDCF8C6)); // màu xanh nhạt
            } else {
                messageLabel.setBackground(new Color(0xFFFFFF)); // trắng
            }

            // Tính toán chiều rộng dựa theo độ dài nội dung + giới hạn max
            int maxWidth = (int) (this.getWidth() * 0.6);
            int minWidth = 50;
            FontMetrics fm = messageLabel.getFontMetrics(messageLabel.getFont());
            int textWidth = fm.stringWidth(message);
            int bubbleWidth = Math.min(maxWidth, Math.max(minWidth, textWidth + 30));

            messageLabel.setMaximumSize(new Dimension(bubbleWidth, Integer.MAX_VALUE));
            messageLabel.setPreferredSize(new Dimension(bubbleWidth, messageLabel.getPreferredSize().height));
            messageLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));

            messagePanel.add(messageLabel);
            messageContainer.add(messagePanel);
            messageContainer.add(Box.createVerticalStrut(4));

            messageContainer.revalidate();
            messageContainer.repaint();

            scrollToBottom();
        });
    }


    private void scrollToBottom() {
        JScrollBar vertical = ((JScrollPane) this.getComponent(0)).getVerticalScrollBar();
        SwingUtilities.invokeLater(() -> vertical.setValue(vertical.getMaximum()));
    }





    private JPanel createMessageBubble(String sender, String message) {
        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());
        bubble.setBackground(sender.equals(currentUsername) ? new Color(0xDCF8C6) : new Color(0xFFFFFF));
        bubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel messageLabel = new JLabel("<html><body style='width: 100%'>" + message + "</body></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK);

        bubble.add(messageLabel, BorderLayout.CENTER);

        // Set max width to 60% screen
        int maxWidth = (int) (getWidth() * 0.6);
        messageLabel.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        bubble.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        bubble.setAlignmentX(sender.equals(currentUsername) ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        // Rounded background
        bubble.setOpaque(true);
        bubble.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 8, 4, 8),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true)
        ));

        return bubble;
    }



    private void loadChatHistory() {
        String toUsername = "staff";
        messageService.getChatHistory(currentUsername, toUsername, messages -> {
            SwingUtilities.invokeLater(() -> {
                for (MessageDTO msg : messages) {
                    appendMessage(msg.getSender(), msg.getContent());
                }
            });
        });
    }
}
