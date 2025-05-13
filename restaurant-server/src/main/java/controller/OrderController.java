package controller;

import dto.OrderDTO;
import dto.OrderSummaryDTO;
import network.CommandType;
import network.JsonRequest;
import network.JsonResponse;
import service.OrderService;
import socketserver.ClientHandler;

import java.util.List;
import java.util.Map;

public class OrderController {

    private final OrderService orderService = new OrderService();
    public static int orderId;

    // 1. Lấy danh sách đơn hàng (tóm tắt)
    public void getAllOrderSummaries(JsonRequest request, ClientHandler client) {
        try {
            List<OrderSummaryDTO> summaries = orderService.getAllOrderSummaries();
            System.out.println("[DEBUG] Số lượng item tìm thấy: " + summaries.size());

            JsonResponse response = new JsonResponse(
                    CommandType.GET_ORDERS_SUCCESS.name(),
                    summaries,
                    "server"
            );
            client.sendResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            client.sendResponse(new JsonResponse(
                    CommandType.GET_ORDERS_FAIL.name(),
                    "Không thể lấy danh sách đơn hàng: " + e.getMessage()
            ));
        }
    }

    // 2. Lấy chi tiết đơn hàng theo orderId
    public void getOrderItemsByOrderId(JsonRequest request, ClientHandler client) {
        try {
            orderId = (int) request.getData(); // giả định client gửi Integer
            System.out.println("[DEBUG] Đang xử lý GET_ORDER_ITEMS cho orderId = " + orderId);

            OrderDTO orderDetail = orderService.getOrderDetail(orderId);

            if (orderDetail == null) {
                client.sendResponse(new JsonResponse(
                        CommandType.GET_ORDER_ITEMS_FAIL.name(),
                        "Không tìm thấy đơn hàng với ID: " + orderId
                ));
                return;
            }

            JsonResponse response = new JsonResponse(
                    CommandType.GET_ORDER_ITEMS_SUCCESS.name(),
                    orderDetail,
                    "server"
            );
            client.sendResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            client.sendResponse(new JsonResponse(
                    CommandType.GET_ORDER_ITEMS_FAIL.name(),
                    "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage()
            ));
        }
    }
    public void updateOrderStatus(JsonRequest request, ClientHandler client) {
        try {
            Map<String, Object> data = (Map<String, Object>) request.getData();
            int orderId = (int) data.get("orderId");
            String statusStr = (String) data.get("status");

            OrderSummaryDTO.OrderStatus newStatus = OrderSummaryDTO.OrderStatus.valueOf(statusStr);
            boolean success = orderService.updateOrderStatus(orderId, newStatus);

            if (success) {
                client.sendResponse(new JsonResponse("UPDATE_ORDER_STATUS_SUCCESS", null));
                System.out.println("✅ Đã cập nhật trạng thái đơn hàng " + orderId + " thành " + newStatus);
            } else {
                client.sendResponse(new JsonResponse("UPDATE_ORDER_STATUS_FAIL", "❌ Không tìm thấy đơn hàng."));
            }
        } catch (IllegalArgumentException e) {
            client.sendResponse(new JsonResponse("UPDATE_ORDER_STATUS_FAIL", "❌ Trạng thái không hợp lệ."));
        } catch (Exception e) {
            e.printStackTrace();
            client.sendResponse(new JsonResponse("UPDATE_ORDER_STATUS_FAIL", "❌ Lỗi server: " + e.getMessage()));
        }
    }

}
