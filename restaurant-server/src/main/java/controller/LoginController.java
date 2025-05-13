package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CustomerDTO;
import dto.LoginDTO;
import network.JsonRequest;
import network.JsonResponse;
import service.CustomerService;
import session.SessionManager;
import socketserver.ClientHandler;

public class LoginController {
    private final CustomerService customerService;
    static CustomerDTO customerDTO;

    public LoginController() {
        this.customerService = new CustomerService();
    }

    public void handleLoginRequest(JsonRequest request, ClientHandler clientHandler) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoginDTO loginDTO = objectMapper.convertValue(request.getData(), LoginDTO.class);

            customerDTO = customerService.login(loginDTO.getUsername(), loginDTO.getPassword());

            if (customerDTO != null) {
                clientHandler.setUsername(customerDTO.getUserName());

                JsonResponse response = new JsonResponse("LOGIN_SUCCESS", customerDTO, "server");
                SessionManager.instance().setCustomerId(customerDTO.getCustomerId());
                clientHandler.sendResponse(response);
            } else {
                JsonResponse response = new JsonResponse("LOGIN_FAILED", null, "server");
                clientHandler.sendResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponse errorResponse = new JsonResponse("LOGIN_ERROR", null, "server");
            clientHandler.sendResponse(errorResponse);
        }
    }
}