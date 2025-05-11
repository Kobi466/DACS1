package repositoy_dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import model.Reservation;
import model.TableBooking;
import org.hibernate.Session;
import util.HibernateUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TableBookingDAO extends AbstractDAO<TableBooking, Integer> {
    public TableBookingDAO() {
        super(TableBooking.class);
    }
    public static TableBookingDAO getInstance(){
        return new TableBookingDAO();
    }
    public TableBooking findAvailableTable(LocalDateTime time) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Lấy tất cả bàn TRỐNG
            List<TableBooking> tables = session.createQuery(
                            "FROM TableBooking tb WHERE tb.status = :status", TableBooking.class)
                    .setParameter("status", TableBooking.StatusTable.TRONG)
                    .list();

            // Lọc bàn không có reservation trùng giờ
            for (TableBooking tb : tables) {
                boolean hasConflict = tb.getReservations().stream().anyMatch(res -> {
                    long diff = Duration.between(res.getBookingTime(), time).abs().toMinutes();
                    return diff < 90 && res.getStatus() != Reservation.ReservationStatus.HUY;
                });
                if (!hasConflict) return tb;
            }
            return null;
        }
    }
    public TableBooking findAvailableByCode(String code, TableBooking.StatusTable status) {
        try {
            EntityManager em = HibernateUtil.getSessionFactory().createEntityManager();
            return em.createQuery(
                            "SELECT t FROM TableBooking t WHERE t.tableName = :code AND t.status = :status", TableBooking.class)
                    .setParameter("code", code)
                    .setParameter("status", status)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
