package service;

import session.ChatHistoryRequest;
import dto.MessageDTO;
import network.JsonRequest;
import network.JsonResponse;
import socket.SocketClient;
import session.SessionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

public class CustomerMessageService {

    // Gửi tin nhắn từ Customer tới Staff
    public void sendMessage(String fromUsername, String toUsername, String content) {
        // Gán đúng customerId từ session
        int customerId = SessionManager.instance().getCustomerId();
        if (customerId <= 0) {
            System.err.println("❌ [Client] Không thể gửi tin nhắn, customerId không hợp lệ");
            return;
        }

        MessageDTO message = new MessageDTO(
                fromUsername,
                toUsername,
                content,
                LocalDateTime.now(),
                customerId
        );

        JsonRequest request = new JsonRequest("SEND_MESSAGE", message, fromUsername);
        SocketClient.sendRequest(request); // Địa chỉ và cổng của server
    }

    // Lấy lịch sử tin nhắn giữa Customer và Staff
    public void getChatHistory(String fromUsername, String toUsername, Consumer<List<MessageDTO>> callback) {
        int customerId = SessionManager.instance().getCustomerId();
        if (customerId <= 0) {
            System.err.println("❌ [Client] Không thể lấy lịch sử chat, customerId không hợp lệ");
            return;
        }

        JsonRequest request = new JsonRequest("GET_CHAT_HISTORY",
                new ChatHistoryRequest(fromUsername, toUsername),
                fromUsername);
        SocketClient.sendRequest(request); // Địa chỉ và cổng của server

        JsonResponse response = SocketClient.readResponse();
        if (response != null && "CHAT_HISTORY".equals(response.getStatus())) {
            List<MessageDTO> messages = (List<MessageDTO>) response.getData(); // Dữ liệu chat từ server
            System.out.println("🟢 Debug: Chat history size: " + (messages != null ? messages.size() : 0));
            if (callback != null) callback.accept(messages);
        } else {
            System.err.println("❌ [Client] Không nhận được phản hồi hợp lệ hoặc lỗi load chat history.");
        }
    }public void listenForMessages(String host, int port) {
//        SocketClient.listenToServer( new RealTimeResponseHandler());
    }
}
