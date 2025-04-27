package dao;

import model.TableBooking;

import java.util.List;

public class TableBookingDAO extends AbstractDAO<TableBooking, Integer> {
    public TableBookingDAO() {
        super(TableBooking.class);
    }
    public static TableBookingDAO getInstance(){
        return new TableBookingDAO();
    }
    /**
     * Lấy các bàn/phòng còn trống
     */
    public List<TableBooking> getAvailableTables() {
        return em.createQuery(
                        "FROM TableBooking WHERE status = 'TRỐNG'", TableBooking.class)
                .getResultList();
    }

    /**
     * Cập nhật trạng thái bàn
     */
    public void updateStatus(TableBooking table, String newStatus) {
        try {
            em.getTransaction().begin();
            table.setStatus(TableBooking.StatusTable.valueOf(newStatus));
            em.merge(table);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
