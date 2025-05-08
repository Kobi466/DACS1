package dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tính tương thích khi serialize/deserialize

    private int orderId;
    private int customerId;          // Lấy ID thay vì toàn bộ đối tượng Customer
    private int tableId;             // Tương tự cho TableBooking
    private LocalDateTime orderDate;
    private String status;           // Hoặc dùng Enum nếu bạn chia sẻ Enum trong common
    private List<OrderItemDTO> orderItems; // Nếu muốn bao gồm chi tiết món ăn

    private RatingDTO rating;        // Có thể nullable, không bắt buộc gửi

    public OrderDTO() {}

    public OrderDTO(int orderId, int customerId, int tableId, LocalDateTime orderDate, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.tableId = tableId;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getters & Setters

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public RatingDTO getRating() {
        return rating;
    }

    public void setRating(RatingDTO rating) {
        this.rating = rating;
    }
}
