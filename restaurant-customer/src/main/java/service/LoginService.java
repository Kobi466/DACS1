package service;

import dto.CustomerDTO;
import dto.LoginDTO;
import network.JsonRequest;
import network.JsonResponse;
import socket.SocketClient;

import java.util.Map;

public class LoginService {

    public static JsonResponse login(String username, String password) {
        JsonRequest request = new JsonRequest("LOGIN", new LoginDTO(username, password), username);
        SocketClient.sendRequest(request); // Địa chỉ và cổng của server
        JsonResponse response = SocketClient.readResponse();

        if (response != null && "LOGIN_SUCCESS".equals(response.getStatus())) {
        } else {
            System.err.println("❌ LoginService: Đăng nhập thất bại hoặc phản hồi không hợp lệ.");
        }

        return response;
    }
    public static JsonResponse register(String username, String password, String sdt) {

        JsonRequest request = new JsonRequest("REGISTER", new CustomerDTO(username, password, sdt), username);
        SocketClient.sendRequest(request); // Địa chỉ và cổng của server
        JsonResponse response = SocketClient.readResponse();

        if (response != null && "REGISTER_SUCCESS".equals(response.getStatus())) {
        } else {
            System.err.println("❌ LoginService: Đăng ký thất bại hoặc phản hồi không hợp lệ.");
        }

        return response;
    }
}
