package dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String foodName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    // Constructors, Getters, Setters
    public OrderItemDTO() {
    }

    public OrderItemDTO(String name, int quantity, BigDecimal unitPrice, BigDecimal multiply) {
        this.foodName = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = multiply;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
