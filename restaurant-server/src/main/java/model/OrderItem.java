package model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orderItems")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;
    private int quantity;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "nemu_item_id")
    private MenuItem menuItem;
    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;
    public OrderItem() {
    }

    public OrderItem(int quantity, BigDecimal price, MenuItem menuItem, Order order) {
        this.quantity = quantity;
        this.price = price;
        this.menuItem = menuItem;
        this.order = order;
    }

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

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
