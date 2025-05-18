package service;

import dto.MessageDTO;
import network.JsonRequest;
import session.ChatHistoryRequest;
import socket.SocketClient;
import network.CommandType;

import javax.swing.*;
import java.time.LocalDateTime;

public class ChatService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    // ✅ Thêm biến lưu tên staff hiện tại
    public static String currentStaffName = "staff"; // hoặc set sau khi đăng nhập

    // Gửi tin nhắn
    public static void sendMessage(String sender, String receiver, String content) {
        MessageDTO message = new MessageDTO();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now().toString());

        JsonRequest request = new JsonRequest(CommandType.SEND_MESSAGE.name(), message);
        SocketClient.sendRequest(request, SERVER_HOST, SERVER_PORT);
    }

    public static void notifyStaffOnline() {
        JsonRequest request = new JsonRequest(CommandType.STAFF_JOINED.name(), currentStaffName);
        SocketClient.sendRequest(request, SERVER_HOST, SERVER_PORT);
    }

    // Lấy danh sách khách hàng
    public static void loadCustomerList() {
        JsonRequest request = new JsonRequest(CommandType.GET_CUSTOMER_LIST.name(), null);
        SocketClient.sendRequest(request, SERVER_HOST, SERVER_PORT);
    }

    // ✅ Lấy lịch sử chat với khách hàng (có truyền staff)
    public static void loadChatHistory(String selectedCustomer) {
        if (selectedCustomer == null || currentStaffName == null) {
            System.out.println("❌ Thiếu customer hoặc staff khi lấy lịch sử chat.");
            return;
        }

        ChatHistoryRequest data = new ChatHistoryRequest();
        data.setCustomer(selectedCustomer);
        data.setStaff(currentStaffName); // thêm staff vào

        System.out.println("📤 Gửi yêu cầu lịch sử chat cho customer: " + selectedCustomer + ", staff: " + currentStaffName);
        JsonRequest request = new JsonRequest(CommandType.GET_CHAT_HISTORY.name(), data);
        SocketClient.sendRequest(request, SERVER_HOST, SERVER_PORT);
    }

    // Hiển thị tin nhắn lên chat area
    public static void appendMessageToChat(JTextArea chatArea, String sender, String content, String sentAt) {
        chatArea.append(String.format("[%s] %s: %s\n", sentAt, sender, content));
    }
}
