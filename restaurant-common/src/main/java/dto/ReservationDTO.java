package dto;

import java.time.LocalDateTime;

public class ReservationDTO implements java.io.Serializable{
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tính tương thích khi serialize/deserialize

    private int reservationId;
    private LocalDateTime bookingTime;
    private String status; // Chuyển kiểu enum thành String
    private int customerId;  // ID của khách hàng
    private int tableBookingId;  // ID của bàn

    // Constructor
    public ReservationDTO(int reservationId, LocalDateTime bookingTime, String status, int customerId, int tableBookingId) {
        this.reservationId = reservationId;
        this.bookingTime = bookingTime;
        this.status = status;
        this.customerId = customerId;
        this.tableBookingId = tableBookingId;
    }

    // Getter và Setter
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getTableBookingId() {
        return tableBookingId;
    }

    public void setTableBookingId(int tableBookingId) {
        this.tableBookingId = tableBookingId;
    }
}
