package socket;

import dto.MessageDTO;
import network.JsonResponse;
import view.ChatPanel;

import javax.swing.*;
import java.util.List;

//import static view.ChatPanel.appendMessage;


public class RealTimeResponseHandler implements ResponseHandler {

    @Override
    public void handleResponse(JsonResponse response) {
        switch (response.getStatus()) {
            case "NEW_MESSAGE" -> {
                MessageDTO newMessage = (MessageDTO) response.getData();
                if (newMessage != null) {
                    System.out.println("[SocketClient] Tin nhắn mới từ: " + newMessage.getSender());
                    updateChatUI(newMessage); // Gọi hàm để cập nhật UI
                }
            }
            case "MESSAGE_SENT" -> {
                System.out.println("[SocketClient] Message sent successfully!");
            }
            case "CHAT_HISTORY" -> {
                List<MessageDTO> chatHistory = (List<MessageDTO>) response.getData();
                System.out.println("[SocketClient] Chat history loaded: " + chatHistory.size() + " messages.");
                updateChatHistoryUI(chatHistory); // Cập nhật giao diện lịch sử chat
            }
            default -> {
                System.err.println("[SocketClient] Không xác định trạng thái phản hồi: " + response.getStatus());
            }
        }
    }

    private void updateChatUI(MessageDTO message) {
        SwingUtilities.invokeLater(() -> {
            String sender = message.getSender();
            String content = message.getContent();


            // Giả sử ChatPanel có JTextArea tên là chatArea
            ChatPanel.getInstance().appendMessage("staff",content);
        });
    }


    private void updateChatHistoryUI(List<MessageDTO> chatHistory) {
        SwingUtilities.invokeLater(() -> {
            for (MessageDTO message : chatHistory) {
                ChatPanel.getInstance().appendMessage(message.getSender(), message.getContent());
            }
        });
    }

}