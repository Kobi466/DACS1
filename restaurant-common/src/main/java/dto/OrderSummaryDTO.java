package dto;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int orderId;
    private String customerName;
    private String customerPhone;
    private LocalDateTime orderDate;
    private double totalPrice;
    private OrderStatus status;

    public OrderSummaryDTO(int orderId, String userName, String sdt, LocalDateTime orderDate, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.customerName = userName;
        this.customerPhone = sdt;
        this.orderDate = orderDate;
        this.status = orderStatus;
    }

    public enum OrderStatus {
        CHO_XAC_NHAN,     // Mới tạo, đang chờ nhân viên xác nhận
        DA_XAC_NHAN,      // Đã được nhân viên xác nhận, chuẩn bị nấu
        DANG_CHE_BIEN,    // Đang được nấu
        HOAN_THANH,    // Đã nấu xong, chờ giao/khách nhận
        DA_HUY;
    }

    // Constructors, Getters, Setters
    public OrderSummaryDTO() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
