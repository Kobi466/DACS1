package session;

import java.io.Serializable;

public class ChatHistoryRequest implements Serializable {
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tương thích dữ liệu giữa các phiên bản.

    private String customer; // Khách hàng yêu cầu
    private String staff;    // Nhà hàng/staff trong yêu cầu

    public ChatHistoryRequest() {
    }

    // Constructor
    public ChatHistoryRequest(String customer, String staff) {
        this.customer = customer;
        this.staff = staff;
    }

    // Getter và Setter
    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }
}