package mapper;

import dto.ReservationOrderDTO;
import model.*;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class ReservationOrderMapper {

    public Reservation toReservation(ReservationOrderDTO dto, Customer customer, TableBooking table) {
        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setTableBooking(table);
        reservation.setBookingTime(LocalDateTime.of(LocalDate.now(), dto.getBookingTime()));
        reservation.setStatus(Reservation.ReservationStatus.CHO_XAC_NHAN);
        return reservation;
    }

    public Order toOrder(ReservationOrderDTO dto, Customer customer, TableBooking table, List<OrderItem> items) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setTable(table);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.CHO_XAC_NHAN);
        order.setOrderItems(items);
        return order;
    }

    public OrderItem toOrderItem(MenuItem menuItem, Order order, int quantity) {
        OrderItem item = new OrderItem();
        item.setMenuItem(menuItem);
        item.setOrder(order);
        item.setQuantity(quantity);
        item.setPrice(menuItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }
}
