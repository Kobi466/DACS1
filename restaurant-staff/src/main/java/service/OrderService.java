package service;

import com.fasterxml.jackson.core.type.TypeReference;
import controller.TableController;
import dto.OrderDTO;
import dto.OrderSummaryDTO;
import network.CommandType;
import network.JsonRequest;
import socket.SocketClient;
import util.JSONUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OrderService {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public void fetchOrders(Consumer<List<OrderSummaryDTO>> onSuccess, Consumer<String> onError) {
        JsonRequest request = new JsonRequest(CommandType.GET_ORDERS.name(), null);
        SocketClient.sendRequest(request, HOST, PORT);

        SocketClient.listenToServer(HOST, PORT, response -> {
            String status = response.getStatus();
            if (CommandType.GET_ORDERS_SUCCESS.name().equals(status)) {
                List<OrderSummaryDTO> orders = JSONUtil.getObjectMapper().convertValue(
                        response.getData(), new TypeReference<>() {}
                );
                onSuccess.accept(orders);
            } else if (CommandType.GET_ORDERS_FAIL.name().equals(status)) {
                onError.accept("❌ Không thể tải danh sách đơn hàng.");
            }
        });
    }
    public void updateOrderStatus(int orderId, OrderSummaryDTO.OrderStatus status, Runnable onSuccess, Consumer<String> onError) {
        JsonRequest request = new JsonRequest(CommandType.UPDATE_ORDER_STATUS.name(),
                Map.of("orderId", orderId, "status", status.name()));
        SocketClient.sendRequest(request, HOST, PORT);
        System.out.println("Gửi UPDATE_ORDER_STATUS: orderId=" + orderId + ", status=" + status.name());
        SocketClient.listenToServer(HOST, PORT, response -> {
            String responseStatus = response.getStatus();
            if (CommandType.UPDATE_ORDER_STATUS_SUCCESS.name().equals(responseStatus)) {
                onSuccess.run();
            } else if (CommandType.UPDATE_ORDER_STATUS_FAIL.name().equals(responseStatus)) {
                onError.accept("❌ Cập nhật trạng thái đơn hàng thất bại.");
            }
        });
    }


    public void fetchOrderDetail(int orderId, Consumer<OrderDTO> onSuccess, Consumer<String> onError) {
        JsonRequest request = new JsonRequest(CommandType.GET_ORDER_ITEMS.name(), orderId);
        SocketClient.sendRequest(request, HOST, PORT);

        SocketClient.listenToServer(HOST, PORT, response -> {
            String status = response.getStatus();
            if (CommandType.GET_ORDER_ITEMS_SUCCESS.name().equals(status)) {
                OrderDTO order = JSONUtil.getObjectMapper().convertValue(response.getData(), OrderDTO.class);
                onSuccess.accept(order);
            } else if (CommandType.GET_ORDER_ITEMS_FAIL.name().equals(status)) {
                onError.accept("❌ Không thể lấy chi tiết đơn hàng.");
            }
        });
    }
}
