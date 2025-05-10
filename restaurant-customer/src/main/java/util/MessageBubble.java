package util;

import util.RoundedBorder;

import javax.swing.*;
import java.awt.*;

public class MessageBubble extends JPanel {
    public MessageBubble(String message, boolean isSender) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JTextArea textArea = new JTextArea(message);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(Color.BLACK);
        textArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Khung bong bóng
        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());
        bubble.add(textArea, BorderLayout.CENTER);
        bubble.setBackground(isSender ? new Color(0xDCF8C6) : new Color(0xF1F0F0));
        bubble.setOpaque(true);
        bubble.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // Bo góc
        bubble.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20, isSender),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        // Cho phép co giãn theo nội dung
        bubble.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        bubble.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bọc lại bằng BoxLayout để căn trái/phải và chiều rộng co giãn tự nhiên
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setOpaque(false);
        if (isSender) {
            container.add(Box.createHorizontalGlue());
            container.add(bubble);
        } else {
            container.add(bubble);
            container.add(Box.createHorizontalGlue());
        }

        add(container, BorderLayout.CENTER);
    }
}
