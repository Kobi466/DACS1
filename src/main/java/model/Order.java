package model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int order_Id;
    @ManyToOne
    @JoinColumn(name = "customer_Id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "table_Id")
    private TableBooking table;
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Rating rating;
    public enum OrderStatus {
        KếtThúc, XácNhận, HoànThành
    }

    public Order(int order_Id, Customer customer, TableBooking table, LocalDateTime orderDate, OrderStatus status, List<OrderItem> orderItems, Rating rating) {
        this.order_Id = order_Id;
        this.customer = customer;
        this.table = table;
        this.orderDate = orderDate;
        this.status = status;
        this.orderItems = orderItems;
        this.rating = rating;
    }

    public Order(Customer customer, TableBooking table, LocalDateTime orderDate, OrderStatus status, List<OrderItem> orderItems) {
        this.customer = customer;
        this.table = table;
        this.orderDate = orderDate;
        this.status = status;
        this.orderItems = orderItems;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Order() {
    }

    public int getOrder_Id() {
        return order_Id;
    }

    public void setOrder_Id(int order_Id) {
        this.order_Id = order_Id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public TableBooking getTable() {
        return table;
    }

    public void setTable(TableBooking table) {
        this.table = table;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_Id=" + order_Id +
                ", customer=" + customer +
                ", table=" + table +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", orderItems=" + orderItems +
                ", rating=" + rating +
                '}';
    }
}

