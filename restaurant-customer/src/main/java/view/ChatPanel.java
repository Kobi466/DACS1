package view;

import controller.MessageController;
import dto.MessageDTO;
import service.CustomerMessageService;
import util.RoundedTextArea;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


public class ChatPanel extends JPanel {

    private JPanel messageContainer; // Chứa toàn bộ tin nhắn
    public JTextField inputField;
    private JButton sendButton;
    private CustomerMessageService messageService;
    public String currentUsername;
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
        JButton suggestBtn = new JButton("Cú pháp");
        inputPanel.add(suggestBtn, BorderLayout.WEST);
        suggestBtn.addActionListener(e -> {
            JPopupMenu menu = new JPopupMenu();
            String[] suggestions = {
                    "đặt bàn [ngày/tháng] lúc [hh:mm] [BAN1->8/PHONGVIP1->6] [món ăn] [số lượng] xuất",
                    "Cho tôi xem menu quán",
                    "Cho tôi xem những bàn trống hiện tại của nhà hàng"
            };

            for (String s : suggestions) {
                JMenuItem item = new JMenuItem(s);
                item.addActionListener(ev -> inputField.setText(s));
                menu.add(item);
            }

            menu.show(suggestBtn, 0, suggestBtn.getHeight());
        });
        add(inputPanel, BorderLayout.SOUTH);
        ActionListener ac = new MessageController(this, messageService);

        sendButton.addActionListener(ac);
        inputField.addActionListener(ac);
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
}
