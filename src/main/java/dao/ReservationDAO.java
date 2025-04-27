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
    public List<Reservation> findCho_Xac_NhanReservations() {
        return em.createQuery(
                        "FROM Reservation r WHERE r.status = :status AND r.bookingTime >= :now",
                        Reservation.class)
                .setParameter("status", Reservation.ReservationStatus.CHO_XAC_NHAN)
                .setParameter("now", LocalDateTime.now())
                .getResultList();
    }
    public void updateReservationStatus(Integer reservationId, Reservation.ReservationStatus newStatus) {
        try {
            em.getTransaction().begin();

            Reservation reservation = em.createQuery(
                            "FROM Reservation r WHERE r.reservationId = :id",
                            Reservation.class)
                    .setParameter("id", reservationId)
                    .getSingleResult();

            reservation.setStatus(newStatus);
            em.merge(reservation);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Không thể cập nhật trạng thái đặt bàn: " + e.getMessage());
        }
    }
    public List<Reservation> getCurrentReservations() {
        LocalDateTime now = LocalDateTime.now();
        return em.createQuery(
            """
            FROM Reservation r 
            WHERE r.status IN (:statusList) 
            AND r.bookingTime BETWEEN :startTime AND :endTime 
            ORDER BY r.bookingTime ASC
            """, Reservation.class)
            .setParameter("statusList", List.of(
                Reservation.ReservationStatus.CHO_XAC_NHAN,
                Reservation.ReservationStatus.DA_XAC_NHAN
            ))
            .setParameter("startTime", now)
            .setParameter("endTime", now.plusHours(24))
            .getResultList();
    }
}