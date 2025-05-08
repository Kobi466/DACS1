package model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kitchen_queue")
public class KitchenQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private KitchenStatus status; // CHO_NAU, DANG_NAU, DA_XONG

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;
    public enum KitchenStatus {
        CHO_NAU, DANG_NAU, DA_XONG
    }

    public KitchenQueue() {
    }

    public KitchenQueue(KitchenStatus status, LocalDateTime updatedAt, OrderItem orderItem) {
        this.status = status;
        this.updatedAt = updatedAt;
        this.orderItem = orderItem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public KitchenStatus getStatus() {
        return status;
    }

    public void setStatus(KitchenStatus status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}

