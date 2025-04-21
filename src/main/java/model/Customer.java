package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int customer_Id;
    @Column(unique = true, nullable = false)
    private String userName;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String sdt;
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;
    @OneToMany(mappedBy = "customer")
    private List<Reservation> reservations;
    @OneToMany(mappedBy = "customer")
    private List<Rating> ratings;
    @OneToMany(mappedBy = "customer")
    private List<CustomerVoucher> customerVouchers;
    @OneToMany(mappedBy = "customer")
    private List<Message> messages;

    public Customer(String userName, String password, String sdt, List<Order> orders, List<Reservation> reservations, List<Rating> ratings, List<CustomerVoucher> customerVouchers, List<Message> messages) {
        this.userName = userName;
        this.password = password;
        this.sdt = sdt;
        this.orders = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.customerVouchers = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public Customer(String userName, String password, String sdt) {
        this.userName = userName;
        this.password = password;
        this.sdt = sdt;
    }

    public Customer() {
    }

    public Customer(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSdt() {
        return sdt;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<CustomerVoucher> getCustomerVouchers() {
        return customerVouchers;
    }

    public void setCustomerVouchers(List<CustomerVoucher> customerVouchers) {
        this.customerVouchers = customerVouchers;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getCustomer_Id() {
        return customer_Id;
    }

    public void setCustomer_Id(int customer_Id) {
        this.customer_Id = customer_Id;
    }

    public void setSdt(String input) {
        // Trường hợp 1: Nhập 0366500642
        if (input.matches("^0\\d{9}$")) {
            this.sdt = "+84" + input.substring(1);
        }
        // Trường hợp 2: Nhập 84366500642
        else if (input.matches("^84\\d{9}$")) {
            this.sdt = "+" + input;
        }
        // Trường hợp 3: Nhập +84366500642
        else if (input.matches("^\\+84\\d{9}$")) {
            this.sdt = input;
        } else {
            System.out.println("❌ Số điện thoại không hợp lệ! Chỉ chấp nhận: 036..., 84..., hoặc +84...");
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customer_Id=" + customer_Id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", sdt='" + sdt + '\'' +
                ", orders=" + orders +
                ", reservations=" + reservations +
                ", ratings=" + ratings +
                ", customerVouchers=" + customerVouchers +
                ", messages=" + messages +
                '}';
    }
}
