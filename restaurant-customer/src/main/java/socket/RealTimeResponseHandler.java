package socket;

import dto.MessageDTO;
import network.JsonResponse;

import java.util.List;

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
        // Ví dụ: Hiển thị tin nhắn lên giao diện người dùng
        System.out.println("[Client] Tin nhắn mới: " + message.getContent());
        // TODO: Tích hợp với UI để hiển thị tin nhắn trên giao diện người dùng
    }

    private void updateChatHistoryUI(List<MessageDTO> chatHistory) {
        // TODO: Logic để hiển thị toàn bộ lịch sử chat trên giao diện
        System.out.println("[Client] Cập nhật giao diện với lịch sử chat...");
    }
}