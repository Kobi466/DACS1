package dao;

import model.Customer;
import model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationDAO extends AbstractDAO<Reservation, Integer> {
    public static ReservationDAO getInstance(){
        return new ReservationDAO();
    }
    public ReservationDAO() {
        super(Reservation.class);
    }
    public List<Reservation> findUpcomingByCustomer(Customer customer) {
        return em.createQuery("FROM Reservation WHERE customer = :customer AND bookingTime >= :now", Reservation.class)
                .setParameter("customer", customer)
                .setParameter("now", LocalDateTime.now())
                .getResultList();
    }
}
