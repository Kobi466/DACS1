package controller.customerCtrl;


import dao.CustomerDAO;
import model.Customer;
import view.customerView.CustomerGUI;
import view.customerView.LoginUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TaiKhoanListener implements ActionListener {
    public LoginUI loginUI;
    public Customer taiKhoan;
    public TaiKhoanListener(LoginUI loginUI) {
        this.loginUI = loginUI;
        taiKhoan = new Customer();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Đăng nhập")) {
            String username = loginUI.usernameField.getText();
            String password = new String(loginUI.passwordField.getPassword());

            // Kiểm tra nếu username hoặc password rỗng
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra tài khoản trong CSDL
            Customer kq = CustomerDAO.getInstance().findByUsername(username, password);

            if (kq == null) {
                JOptionPane.showMessageDialog(null, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                new CustomerGUI().setVisible(true);
                loginUI.dispose(); // Đóng cửa sổ đăng nhập sau khi đăng nhập thành công
            }
        }else if(command.equals("<html><u>Thoát</u></html>")){
            System.exit(0);
            loginUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }else if(command.equals("<html><u>Quên mật khẩu</u></html>")){
            loginUI.toggleSidebar();
            loginUI.passwordField.setText("");
        }else if (command.equals("OK")){
            String username = loginUI.usernameField.getText(); // Lấy username từ giao diện
            String oldPassword = new String(loginUI.passwordField.getPassword()); // Lấy mật khẩu cũ
            String newPassword = new String(loginUI.resetpass.getPassword()); // Lấy mật khẩu mới

            // Kiểm tra nếu username hoặc password rỗng
            if (username.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra nếu mật khẩu mới giống mật khẩu cũ
            if (oldPassword.equals(newPassword)) {
                JOptionPane.showMessageDialog(null, "Mật khẩu mới không được trùng với mật khẩu cũ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Thực hiện cập nhật mật khẩu
//            int kq = CustomerDAO.getInstance().update(new Customer(username, newPassword));

//            if (kq==1) {
//                JOptionPane.showMessageDialog(null, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                loginUI.toggleSidebar();
//            } else {
//                JOptionPane.showMessageDialog(null, "Đổi mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
        } else if (command.equals("👁")) {
            if (loginUI.hienMatKhau[0]) {
                loginUI.resetpass.setEchoChar('•');// Đặt ký tự che mật khẩu
                loginUI.hienMatKhau[0] = false;
            } else {
                loginUI.resetpass.setEchoChar((char) 0); // Hiển thị mật khẩu
                loginUI.hienMatKhau[0] = true;
            }
        }
    }
}
