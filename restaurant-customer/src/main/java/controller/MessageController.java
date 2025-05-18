package controller;


import dto.MessageDTO;
import service.CustomerMessageService;
import view.ChatPanel;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageController implements ActionListener {
    private ChatPanel view;
    private CustomerMessageService messageService;
    public MessageController(ChatPanel view, CustomerMessageService messageService) {
        this.view = view;
        this.messageService = messageService;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if("Gửi".equals(command)){
            String content = this.view.inputField.getText().trim();
            if (content.isEmpty()) return;

            String toUsername = "staff";
            messageService.sendMessage(this.view.currentUsername, toUsername, content);

            this.view.appendMessage(this.view.currentUsername, content); // Hiển thị luôn
            this.view.inputField.setText("");
        }
    }
    public void loadChatHistory() {
        String toUsername = "staff";
        messageService.getChatHistory(this.view.currentUsername, toUsername, messages -> {
            SwingUtilities.invokeLater(() -> {
                for (MessageDTO msg : messages) {
                    this.view.appendMessage(msg.getSender(), msg.getContent());
                }
            });
        });
    }

}
