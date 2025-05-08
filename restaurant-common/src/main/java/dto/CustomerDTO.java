package dto;

import java.io.Serializable;

public class CustomerDTO implements Serializable {
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tính tương thích

    private int customerId;
    private String userName;
    private String password;
    private String sdt;

    public CustomerDTO() {}

    public CustomerDTO(int customerId, String userName, String password, String sdt) {
        this.customerId = customerId;
        this.userName = userName;
        this.password = password;
        this.sdt = sdt;
    }

    public CustomerDTO(int customerId, String userName, String password) {
        this.customerId = customerId;
        this.userName = userName;
        this.password = password;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
}
