package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CustomerDTO;
import dto.LoginDTO;
import dto.MessageDTO;
import mapper.CustomerMapper;
import model.Customer;
import network.CommandType;
import network.JsonRequest;
import network.JsonResponse;
import repositoy_dao.CustomerDAO;
import service.CustomerService;
import session.SessionManager;
import socketserver.ClientHandler;
import util.JacksonUtils;

public class LoginController {
    private final CustomerService customerService;
    static CustomerDTO customerDTO;

    public LoginController() {
        this.customerService = new CustomerService();
    }

    public void handleLoginRequest(JsonRequest request, ClientHandler clientHandler) {
        try {
            LoginDTO loginDTO = JacksonUtils.getObjectMapper().convertValue(request.getData(), LoginDTO.class);

            customerDTO = customerService.login(loginDTO.getUsername(), loginDTO.getPassword());

            if (customerDTO != null) {
                clientHandler.setUsername(customerDTO.getUserName());

                JsonResponse response = new JsonResponse(CommandType.LOGIN_SUCCESS.name(), customerDTO, "server");
                SessionManager.instance().setCustomerId(customerDTO.getCustomerId());
                clientHandler.sendResponse(response);
            } else {
                JsonResponse response = new JsonResponse(CommandType.LOGIN_FAIL.name(), null, "server");
                clientHandler.sendResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleRegis(JsonRequest request, ClientHandler clientHandler) {
        try {
            CustomerDTO customerDTO = JacksonUtils.getObjectMapper().convertValue(request.getData(), CustomerDTO.class);

            if (customerDTO != null) {
                boolean isValid = customerService.regis(customerDTO.getUserName(), customerDTO.getPassword(), customerDTO.getSdt());
                if (isValid == true) {
                    JsonResponse response = new JsonResponse(CommandType.REGISTER_SUCCESS.name(), customerDTO, "server");
                    clientHandler.sendResponse(response);
                }
            }else {
                JsonResponse response = new JsonResponse(CommandType.REGISTER_FAIL.name(), null, "server");
                clientHandler.sendResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}