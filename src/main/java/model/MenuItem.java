package model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table (name = "menu_items")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int food_Id;
    private String name;
    private BigDecimal price;
    private int quantity;
    @OneToMany(mappedBy = "menuItem")
    private List<OrderItem> orderItems;

    public MenuItem() {
    }

    public MenuItem(int food_Id, String name, BigDecimal price, int quantity, List<OrderItem> orderItems) {
        this.food_Id = food_Id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.orderItems = orderItems;
    }

    public int getFood_Id() {
        return food_Id;
    }

    public void setFood_Id(int food_Id) {
        this.food_Id = food_Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "food_Id=" + food_Id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", orderItems=" + orderItems +
                '}';
    }
}

