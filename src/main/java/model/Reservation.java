package model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationId;
    private LocalDateTime bookingTime;
    @Enumerated(EnumType.STRING)    // CHO_XAC_NHAN, DA_XAC_NHAN, HUY

    private ReservationStatus status;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableBooking tableBooking;
    public enum ReservationStatus {
        ChờXácNhận, ĐãXácNhận, Hủy
    }

    public Reservation() {
    }

    public Reservation(LocalDateTime bookingTime, ReservationStatus status, Customer customer, TableBooking tableBooking) {
        this.bookingTime = bookingTime;
        this.status = status;
        this.customer = customer;
        this.tableBooking = tableBooking;
    }

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

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public TableBooking getTableBooking() {
        return tableBooking;
    }

    public void setTableBooking(TableBooking tableBooking) {
        this.tableBooking = tableBooking;
    }
}

