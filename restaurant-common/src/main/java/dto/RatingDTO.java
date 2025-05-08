package dto;

import java.io.Serializable;

public class RatingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int ratingId;
    private int stars;  // Số sao từ 1 đến 5
    private String comment;
    private int customerId;  // ID của khách hàng, tránh việc truyền đối tượng Customer
    private int orderId;     // ID của đơn hàng, tránh việc truyền đối tượng Order

    // Constructor
    public RatingDTO(int ratingId, int stars, String comment, int customerId, int orderId) {
        this.ratingId = ratingId;
        this.stars = stars;
        this.comment = comment;
        this.customerId = customerId;
        this.orderId = orderId;
    }

    // Getter và Setter
    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
