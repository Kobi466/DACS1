package controller;

import dto.CustomerDTO;
import model.Order;
import service.ReservationOrderCombinedService;
import session.ChatHistoryRequest;
import dto.MessageDTO;
import network.JsonRequest;
import network.JsonResponse;
import service.MessageService;
import socketserver.ClientHandler;
import util.JacksonUtils;
import util.ReservationOrderParser;


import java.time.LocalDateTime;
import java.util.List;

public class MessageController {
    private final MessageService messageService = new MessageService();

    public void handleSendMessage(JsonRequest request, ClientHandler senderHandler) {
        try {
            if (request == null || request.getData() == null) {
                senderHandler.sendResponse(new JsonResponse("ERROR", "Request data is invalid or missing", "server"));
                return;
            }

            // Chuyển đổi JsonRequest thành MessageDTO
            MessageDTO messageDTO = JacksonUtils.getObjectMapper().convertValue(request.getData(), MessageDTO.class);

            if (messageDTO == null) {
                senderHandler.sendResponse(new JsonResponse("ERROR", "Failed to parse message data", "server"));
                return;
            }

            // Lưu tin nhắn vào cơ sở dữ liệu
            boolean success = messageService.saveMessage(messageDTO);

            if (success) {
                senderHandler.sendResponse(new JsonResponse("MESSAGE_SENT", "Message saved successfully", "server"));
                // Phát tin nhắn real-time tới receiver
                senderHandler.broadcastMessage(messageDTO);
                System.out.println("📤 Gửi tin nhắn từ " + messageDTO.getSender() + " đến " + messageDTO.getReceiver());
            } else {
                senderHandler.sendResponse(new JsonResponse("ERROR", "Cannot save message", "server"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            senderHandler.sendResponse(new JsonResponse("ERROR", "Failed to handle message", "server"));
        }
    }

    public void handleGetChatHistory(JsonRequest request, ClientHandler senderHandler) {
        try {
            if (request == null || request.getData() == null) {
                senderHandler.sendResponse(new JsonResponse("ERROR", "Request data is invalid or missing", "server"));
                return;
            }

            ChatHistoryRequest chatHistoryRequest = JacksonUtils.getObjectMapper()
                    .convertValue(request.getData(), ChatHistoryRequest.class);

            if (chatHistoryRequest == null) {
                senderHandler.sendResponse(new JsonResponse("ERROR", "Invalid request data format", "server"));
                return;
            }

            System.out.println("🟢 Debug: Lấy lịch sử chat giữa customer: '"
                    + chatHistoryRequest.getCustomer() + "' và staff: '" + chatHistoryRequest.getStaff() + "'");

            List<MessageDTO> chatHistory = messageService.getChatHistory(chatHistoryRequest);

            if (chatHistory == null || chatHistory.isEmpty()) {
                System.err.println("❌ Không tìm thấy lịch sử chat hoặc danh sách trống.");
            } else {
                System.out.println("🟢 Debug: Số lượng tin nhắn trong lịch sử: " + chatHistory.size());
            }

            JsonResponse response = new JsonResponse("CHAT_HISTORY", chatHistory, "server");
            senderHandler.sendResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            senderHandler.sendResponse(new JsonResponse("ERROR", "Failed to get chat history", "server"));
        }
    }

    public void handleGetCustomerList(JsonRequest request, ClientHandler senderHandler) {
        try {
            System.out.println("🟢 Debug: Xử lý yêu cầu tải danh sách khách hàng từ client.");

            // Gọi lớp service để lấy danh sách khách hàng đã có tin nhắn
            List<CustomerDTO> customers = messageService.getCustomersWithMessages();

            if (customers == null) {
                // Trường hợp không thể truy vấn danh sách khách hàng
                System.err.println("❌ Lỗi: Không thể truy xuất danh sách khách hàng có tin nhắn từ cơ sở dữ liệu.");
                senderHandler.sendResponse(new JsonResponse("ERROR", "Cannot load customer list with messages", "server"));
                return;
            }

            if (customers.isEmpty()) {
                // Trường hợp không có khách hàng nào có tin nhắn
                System.out.println("⚠️ Không tìm thấy khách hàng nào có tin nhắn.");
                senderHandler.sendResponse(
                        new JsonResponse("NO_CUSTOMERS_WITH_MESSAGES", "No customers found with existing messages", "server")
                );
                return;
            }

            // Thành công: gửi danh sách khách hàng có tin nhắn
            System.out.println("🟢 Debug: Số lượng khách hàng tải được: " + customers.size());
            senderHandler.sendResponse(new JsonResponse("GET_CUSTOMER_LIST", customers, "server"));

        } catch (Exception e) {
            e.printStackTrace();
            senderHandler.sendResponse(new JsonResponse("ERROR", "Failed to get customer list", "server"));
        }
    }

    public void handleGetCustomerListWithMessages(JsonRequest request, ClientHandler senderHandler) {
        try {
            System.out.println("🟢 Debug: Xử lý yêu cầu danh sách khách hàng kèm tin nhắn.");

            // Gọi service để lấy danh sách khách hàng cùng tin nhắn gần nhất
            List<MessageDTO> customersAndMessages = messageService.getCustomersWithLastMessages();

            if (customersAndMessages == null || customersAndMessages.isEmpty()) {
                System.err.println("⚠️ Không tìm thấy khách hàng hoặc tin nhắn.");
                senderHandler.sendResponse(
                        new JsonResponse("NO_CUSTOMERS_WITH_MESSAGES", "No customers or messages found", "server")
                );
                return;
            }

            senderHandler.sendResponse(
                    new JsonResponse("GET_CUSTOMERS_WITH_MESSAGES", customersAndMessages, "server")
            );

            System.out.println("🟢 Debug: Trả về danh sách khách hàng và tin nhắn (" + customersAndMessages.size() + ").");
        } catch (Exception e) {
            e.printStackTrace();
            senderHandler.sendResponse(new JsonResponse("ERROR", "Failed to get customer list with messages", "server"));
        }
    }

    public static void notifyCustomer(Order order, String message) {
        String customerUsername = order.getCustomer().getUserName();
        String staffUsername = "staff"; // hoặc định danh nhân viên thực tế nếu có


        MessageDTO messageDTO = new MessageDTO(
                staffUsername,                  // sender
                customerUsername,               // receiver
                message,                 // content
                LocalDateTime.now(),            // thời gian gửi
                order.getCustomer().getCustomer_Id()     // customerId
        );

        // Lưu DB nếu muốn
        MessageService messageService = new MessageService();
        messageService.saveMessage(messageDTO);

        // Gửi socket đến client
        ClientHandler customerHandler = ClientHandler.getClientByUsername(customerUsername);
        if (customerHandler != null) {
            customerHandler.broadcastMessage(messageDTO);
            System.out.println("📢 Đã gửi thông báo tới khách: " + customerUsername);
        } else {
            System.err.println("❌ Không tìm thấy handler cho khách: " + customerUsername);
        }
    }
}