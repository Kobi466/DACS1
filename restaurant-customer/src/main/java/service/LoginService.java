package service;

import network.JsonRequest;
import network.JsonResponse;
import socket.SocketClient;

import java.util.Map;

public class LoginService {

    public static JsonResponse login(String username, String password) {
        JsonRequest request = new JsonRequest("LOGIN", Map.of("username", username, "password", password), username);
        SocketClient.sendRequest(request); // Địa chỉ và cổng của server
        JsonResponse response = SocketClient.readResponse();

        if (response != null && "LOGIN_SUCCESS".equals(response.getStatus())) {
            // Debug dữ liệu trả về
            System.out.println("🟢 LoginService: Response data: " + response.getData());
        } else {
            System.err.println("❌ LoginService: Đăng nhập thất bại hoặc phản hồi không hợp lệ.");
        }

        return response;
    }
}
