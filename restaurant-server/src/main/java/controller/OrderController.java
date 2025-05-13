package controller;

import dto.OrderDTO;
import dto.OrderSummaryDTO;
import network.CommandType;
import network.JsonRequest;
import network.JsonResponse;
import service.OrderService;
import socketserver.ClientHandler;

import java.util.List;

public class OrderController {

    private final OrderService orderService = new OrderService();

    // 1. Lấy danh sách đơn hàng (tóm tắt)
    public void getAllOrderSummaries(JsonRequest request, ClientHandler client) {
        try {
            List<OrderSummaryDTO> summaries = orderService.getAllOrderSummaries();

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
            int orderId = (int) request.getData(); // giả định client gửi Integer

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
}
