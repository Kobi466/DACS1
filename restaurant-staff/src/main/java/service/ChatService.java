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

    // Lấy danh sách khách hàng
    public static void loadCustomerList() {
        JsonRequest request = new JsonRequest(CommandType.GET_CUSTOMER_LIST.name(), null);
        SocketClient.sendRequest(request, SERVER_HOST, SERVER_PORT);
    }

    // Lấy lịch sử chat với khách hàng
    public static void loadChatHistory(String selectedCustomer) {
        if (selectedCustomer == null) return;

        ChatHistoryRequest data = new ChatHistoryRequest();
        data.setCustomer(selectedCustomer);
        JsonRequest request = new JsonRequest(CommandType.GET_CHAT_HISTORY.name(), data);
        SocketClient.sendRequest(request, SERVER_HOST, SERVER_PORT);
    }

    // Xử lý tin nhắn đến từ server
    public static void handleIncomingMessage(MessageDTO message, DefaultListModel<String> customerListModel, JTextArea chatArea, String selectedCustomer) {
        String sender = message.getSender();

        // Nếu là khách hàng mới, thêm vào danh sách khách hàng
        if (!customerListModel.contains(sender)) {
            customerListModel.addElement(sender);
        }

        // Nếu khách hàng được chọn là người gửi tin nhắn, hiển thị tin nhắn đó
        if (selectedCustomer != null && selectedCustomer.equals(sender)) {
            appendMessageToChat(chatArea, sender, message.getContent(), message.getSentAt());
        }
    }

    // Hiển thị tin nhắn lên chat area
    public static void appendMessageToChat(JTextArea chatArea, String sender, String content, String sentAt) {
        chatArea.append(String.format("[%s] %s: %s\n", sentAt, sender, content));
    }
}
