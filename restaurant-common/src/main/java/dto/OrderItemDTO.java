package dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemDTO implements Serializable {
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tính tương thích khi serialize/deserialize

    private int orderId;
    private int quantity;
    private BigDecimal price;
    private int menuItemId; // Trích ID từ MenuItemDTO
    private int orderReferenceId; // Trích ID từ Order

    public OrderItemDTO() {
    }

    public OrderItemDTO(int orderId, int quantity, BigDecimal price, int menuItemId, int orderReferenceId) {
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
        this.menuItemId = menuItemId;
        this.orderReferenceId = orderReferenceId;
    }

    // Getters and Setters

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getOrderReferenceId() {
        return orderReferenceId;
    }

    public void setOrderReferenceId(int orderReferenceId) {
        this.orderReferenceId = orderReferenceId;
    }
}
