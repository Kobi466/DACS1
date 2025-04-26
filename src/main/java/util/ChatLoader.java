package util;

import dao.MessageDAO;
import model.Message;

import javax.swing.*;
import java.util.List;

public class ChatLoader extends SwingWorker<List<Message>, Void> {
    private final String staffUsername = "Staff";
    private final String customerUsername;
    private final JTextArea chatArea;

    public ChatLoader(String customerUsername, JTextArea chatArea) {
        this.customerUsername = customerUsername;
        this.chatArea = chatArea;
    }

    @Override
    protected List<Message> doInBackground() {
        return new MessageDAO().getMessagesBetween(staffUsername, customerUsername);
    }

    @Override
    protected void done() {
        try {
            List<Message> messages = get();
            chatArea.setText(""); // clear cũ

            for (Message msg : messages) {
                String prefix = msg.getSender().equals("Staff") ? "Bạn" : msg.getSender();
                String time = msg.getSent_at().toLocalTime().withSecond(0).toString();
                chatArea.append("[" + time + "] " + prefix + ": " + msg.getContent() + "\n");
            }
        } catch (Exception e) {
            chatArea.append("❌ Lỗi khi tải lịch sử chat.");
        }
    }
}
