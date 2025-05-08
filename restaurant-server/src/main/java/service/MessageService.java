package service;

import dto.CustomerDTO;
import dto.MessageDTO;
import repositoy_dao.CustomerDAO;
import repositoy_dao.MessageDAO;
import session.ChatHistoryRequest;

import java.util.ArrayList;
import java.util.List;

public class MessageService {
    public List<MessageDTO> getChatHistory(ChatHistoryRequest chatRequest) {
        // Tìm customerId nếu thiếu
        String customer = chatRequest.getCustomer();
        if (customer == null || customer.isBlank()) {
            System.err.println("⚠️ Customer không hợp lệ!");
            return new ArrayList<>();
        }
        return MessageDAO.getMessages(chatRequest.getCustomer(), chatRequest.getStaff());
    }

    public List<CustomerDTO> getCustomersWithMessages() {
        // Lấy danh sách khách hàng đã có tin nhắn
        return MessageDAO.getCustomersWithMessages();
    }

    public boolean saveMessage(MessageDTO message) {
        // Check and initialize customerId if it's null or invalid
        if (message.getCustomerId() == null || message.getCustomerId() <= 0) {
            System.out.println("🟡 Debug: customerId chưa được thiết lập hoặc không hợp lệ, tìm customerId dựa trên receiver...");

            if (message.getReceiver() == null || message.getReceiver().isBlank()) {
                System.err.println("❌ Lỗi: Receiver không hợp lệ hoặc không được cung cấp.");
                return false; // Cannot proceed without a valid receiver
            }

            // Try to find customerId using receiver's username
            int resolvedCustomerId = CustomerDAO.getInstance().findCustomerIdByUsername(message.getReceiver());
            if (resolvedCustomerId <= 0) {
                System.err.println("❌ Lỗi: Không tìm thấy customerId tương ứng với receiver '" + message.getReceiver() + "'.");
                return false; // Invalid customerId, abort saving
            }

            message.setCustomerId(resolvedCustomerId); // Update the customerId in the message
        }

        // Save message to DB through DAO
        return MessageDAO.insertMessage(message);
    }
    public List<MessageDTO> getCustomersWithLastMessages() {
        // Lấy danh sách khách hàng kèm tin nhắn gần nhất
        return MessageDAO.getCustomersWithLastMessage();
    }
}
