package service;

import com.fasterxml.jackson.core.type.TypeReference;
import dto.OrderDTO;
import dto.OrderSummaryDTO;
import network.CommandType;
import network.JsonRequest;
import socket.SocketClient;
import util.JSONUtil;

import java.util.List;
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
