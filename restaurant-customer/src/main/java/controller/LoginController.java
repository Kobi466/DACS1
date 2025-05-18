package controller;

import dto.CustomerDTO;
import network.JsonResponse;
import service.LoginService;
import session.SessionManager;
import view.CustomerMainUI;
import view.Login;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController implements ActionListener {
    private Login view;

    public LoginController(Login view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Đăng nhập".equals(command)) {
            String username = this.view.loginUsernameField.getText();
            String password = new String(this.view.loginPasswordField.getPassword());
            JsonResponse response = LoginService.login(username, password);

            if (response != null && "LOGIN_SUCCESS".equals(response.getStatus())) {
                // Debug dữ liệu nhận được từ server

                // Chuyển đổi phản hồi sang CustomerDTO
                CustomerDTO customer = (CustomerDTO) response.getData();

                if (customer != null) {
                    int customerId = customer.getCustomerId();

                    // Kiểm tra giá trị customerId
                    if (customerId > 0) {
                        // Gán customerId vào SessionManager
                        SessionManager.instance().setCustomerId(customerId);
                        System.out.println("✅ Gán ID khách hàng: " + customerId);

                        JOptionPane.showMessageDialog(this.view, "Login successful!");

                        // Mở giao diện chính
                        new CustomerMainUI(username).setVisible(true);
                        this.view.dispose();
                    } else {
                        System.err.println("❌ Dữ liệu customerId không hợp lệ.");
                        JOptionPane.showMessageDialog(this.view, "Login failed. Invalid customerId.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this.view, "Login failed. Invalid response data.");
                }
            } else {
                JOptionPane.showMessageDialog(this.view, "Login failed. Please try again.");
            }
        }
        else if ("Đăng ký".equals(command)) {
            String username = this.view.registerUsernameField.getText();
            String password = new String(this.view.registerPasswordField.getPassword());
            String sdt = this.view.registerPhoneField.getText();
            JsonResponse response = LoginService.register(username, password, sdt);

            if (response != null && "REGISTER_SUCCESS".equals(response.getStatus())) {
               JOptionPane.showMessageDialog(this.view, "Register successful!");
            } else {
                JOptionPane.showMessageDialog(this.view,"Register failed. Please try again.");
            }
        }
    }
}