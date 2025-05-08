package dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KitchenQueueDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String status;
    private String updatedAt; // Đổi từ LocalDateTime sang String
    private int orderItemId;

    // Định dạng ngày giờ chuẩn ISO-8601
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    // Constructor
    public KitchenQueueDTO(int id, String status, LocalDateTime updatedAt, int orderItemId) {
        this.id = id;
        this.status = status;
        this.updatedAt = updatedAt.format(formatter); // Chuyển sang định dạng chuỗi
        this.orderItemId = orderItemId;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getUpdatedAtAsDateTime() {
        return LocalDateTime.parse(updatedAt, formatter); // Chuyển ngược sang LocalDateTime
    }

    public void setUpdatedAtAsDateTime(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt.format(formatter); // Chuyển sang chuỗi
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }
}