package socketserver;

import controller.*;
import dto.MessageDTO;
import network.JsonRequest;
import network.JsonResponse;

import java.io.*;
import java.net.Socket;


import static socketserver.ServerSocketHandler.clientMap;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;
    private final LoginController loginController = new LoginController();
    private final MessageController messageController = new MessageController();
    private final OrderController orderController = new OrderController();
    private final ReservationOrderController reservationOrderController = new ReservationOrderController();
    private String username;
    private final TableStatusController tableStatusController = new TableStatusController();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;

        // Khởi tạo ObjectOutputStream trước ObjectInputStream
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.oos.flush(); // Gửi header ngay trước khi sử dụng
        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    private void handleStaffJoin(JsonRequest request) {
        String staffUsername = (String) request.getData();

        System.out.println("[SERVER] Nhân viên " + staffUsername + " đã tham gia hệ thống.");

        setUsername(staffUsername); // set username và put vào map

        sendResponse(new JsonResponse("STAFF_JOINED", "Đã tham gia thành công"));
    }

    public synchronized void sendResponse(JsonResponse response) {
        try {
            oos.writeObject(response);
            oos.flush(); // Đảm bảo dữ liệu được gửi đi ngay
            oos.reset(); // Reset lại stream
            System.out.println("📤 Đã gửi phản hồi tới client: " + response.getStatus());
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi gửi phản hồi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("✅ ClientHandler bắt đầu xử lý cho client tại " + socket.getInetAddress());

            while (!socket.isClosed()) {
                Object obj = ois.readObject(); // Đọc object từ stream

                if (obj instanceof JsonRequest request) {
                    System.out.println("📩 Nhận yêu cầu từ client: " + request.getCommand());

                    switch (request.getCommand()) {
                        case "LOGIN" -> loginController.handleLoginRequest(request, this);
                        case "SEND_MESSAGE" -> messageController.handleSendMessage(request, this);
                        case "GET_CHAT_HISTORY" -> messageController.handleGetChatHistory(request, this);
                        case "GET_CUSTOMER_LIST" -> {
                            System.out.println("🔍 Xử lý lệnh GET_CUSTOMER_LIST");
                            messageController.handleGetCustomerList(request, this);
                        }
                        case "GET_CUSTOMER_LIST_WITH_MESSAGES" -> {
                            System.out.println("🔍 Xử lý lệnh GET_CUSTOMER_LIST_WITH_MESSAGES");
                            messageController.handleGetCustomerListWithMessages(request, this);
                        }
                        case "STAFF_JOINED" -> this.handleStaffJoin(request);
                        case "RESERVE_AND_ORDER" -> reservationOrderController.handleReserveAndOrder(request, this);
                        case "GET_ORDERS" -> {
                            System.out.println("🔍 Xử lý lệnh GET_ORDERS");
                            orderController.getAllOrderSummaries(request, this);
                        }
                        case "GET_ORDER_ITEMS" -> {
                            System.out.println("🔍 Xử lý lệnh GET_ORDER_ITEMS");
                            orderController.getOrderItemsByOrderId(request, this);
                        }
                        case "NEW_ORDER-CREATED" -> {
                            System.out.println("🔍 Xử lý lệnh NEW_ORDER_CREATED");
                            orderController.getAllOrderSummaries(request, this);
                        }
                        case "UPDATE_ORDER_STATUS" -> {
                            System.out.println("✏️ Xử lý lệnh UPDATE_ORDER_STATUS");
                            orderController.updateOrderStatus(request, this);
                        }
                        case "GET_ALL_TABLE_STATUS" -> tableStatusController.handleGetAllTableStatus(request, this);
                        case "UPDATE_TABLE_STATUS" -> tableStatusController.handleUpdateTableStatus(request, this);
                        case "UPDATE_RESERVATION_STATUS" ->
                                tableStatusController.handleUpdateReservationStatus(request, this);
                        case "UPDATE_ORDER_STATUS_FROM_TABLE" ->
                                tableStatusController.handleUpdateOrderStatus(request, this);

                        default -> System.err.println("⚠️ Lệnh không hợp lệ: " + request.getCommand());
                    }
                } else {
                    System.err.println("⚠️ Dữ liệu từ client không hợp lệ: " + obj);
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("❌ Lỗi trong khi xử lý client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public static ClientHandler getClientByUsername(String username) {
        // Lấy clientHandler từ clientMap theo username
        ClientHandler handler = clientMap.get(username);
        if (handler != null && handler.socket != null && !handler.socket.isClosed()) {
            return handler; // User đang online
        }
        return null; // User không online
    }

    private void closeConnection() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            if (username != null) {
                clientMap.remove(username);
                System.out.println("🗑️ Xóa client " + username + " khỏi clientMap");
            }

            System.out.println("🔌 Kết nối với client đã được đóng.");
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi đóng kết nối với client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcastMessage(MessageDTO message) {
        try {
            ClientHandler receiverHandler = ClientHandler.getClientByUsername(message.getReceiver());

            if (receiverHandler != null) {
                // Phát tin nhắn cho người nhận đang online
                JsonResponse response = new JsonResponse("NEW_MESSAGE", message, "server");
                receiverHandler.sendResponse(response);
                System.out.println("📤 Tin nhắn đã được gửi đến receiver: " + message.getReceiver());
            } else {
                System.err.println("⚠️ Receiver '" + message.getReceiver() + "' không online.");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi broadcast tin nhắn: " + e.getMessage());
        }
    }

    public void setUsername(String username) {
        this.username = username;
        clientMap.put(username, this);
    }

    public String getUsername() {
        return username;
    }
}