package repositoy_dao;

import model.Reservation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class ReservationDAO extends AbstractDAO<Reservation, Integer> {
    public static ReservationDAO getInstance(){
        return new ReservationDAO();
    }
    public ReservationDAO() {
        super(Reservation.class);
    }
    public Reservation findReservationByIdTable(int idTable) {
        Transaction tx = null;
        Reservation reservation = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            String hql = "FROM Reservation r WHERE r.tableBooking.id = :idTable AND r.status = :status";
            reservation = session.createQuery(hql, Reservation.class)
                    .setParameter("idTable", idTable)
                    .setParameter("status", Reservation.ReservationStatus.CHO_XAC_NHAN)
                    .setMaxResults(1) // Tránh lỗi nếu có nhiều kết quả
                    .uniqueResult();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }

        return reservation;
    }
    public Reservation findReservationByIdTable2(int idTable) {
        Transaction tx = null;
        Reservation reservation = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            String hql = "FROM Reservation r WHERE r.tableBooking.id = :idTable AND r.status = :status";
            reservation = session.createQuery(hql, Reservation.class)
                    .setParameter("idTable", idTable)
                    .setParameter("status", Reservation.ReservationStatus.DA_XAC_NHAN)
                    .setMaxResults(1) // Tránh lỗi nếu có nhiều kết quả
                    .uniqueResult();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }

        return reservation;
    }

}