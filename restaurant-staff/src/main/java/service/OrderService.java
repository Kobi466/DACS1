package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.OrderSummaryDTO;
import network.CommandType;
import network.JsonRequest;
import network.JsonResponse;
import socket.SocketClient;
import util.JSONUtil;

import java.util.List;
import java.util.function.Consumer;

public class OrderService {

    private final String host = "localhost";
    private final int port = 8080;

    public void fetchOrders(Consumer<List<OrderSummaryDTO>> onSuccess, Consumer<String> onError) {
        JsonRequest request = new JsonRequest();
        request.setCommand(CommandType.GET_ORDERS.name());
        request.setData(null);

        SocketClient.sendRequest(request, host, port);

        SocketClient.listenToServer(host, port, response -> {
            if (CommandType.GET_ORDERS_SUCCESS.name().equals(response.getStatus())) {
                List<OrderSummaryDTO> orders = JSONUtil.getObjectMapper().convertValue(
                        response.getData(), new TypeReference<List<OrderSummaryDTO>>() {}
                );
                onSuccess.accept(orders);
            } else if (CommandType.GET_ORDERS_FAIL.name().equals(response.getStatus())) {
                onError.accept("Lỗi khi tải đơn hàng!");
            }
        });
    }
}
