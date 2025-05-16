package repositoy_dao;

import dto.TableStatusDTO;
import jdk.jfr.consumer.RecordedEvent;
import mapper.TableStatusMapper;
import model.Order;
import model.Reservation;
import model.TableBooking;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TableStatusDAO {

    private static final Logger logger = Logger.getLogger(TableStatusDAO.class.getName());

    /**
     * Trả về danh sách trạng thái của tất cả bàn (bao gồm thông tin đặt chỗ và đơn hàng nếu có).
     */
    public List<TableStatusDTO> getAllTableStatus() {
        List<TableStatusDTO> result = new ArrayList<>();
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Lấy danh sách tất cả các bàn
            List<TableBooking> tables = session.createQuery("from TableBooking", TableBooking.class).list();

            for (TableBooking table : tables) {
                List<Reservation> reservations = getActiveReservationsByTable(session, table.getId());
                List<Order> orders = getActiveOrdersByTable(session, table.getId());

                TableStatusDTO dto = TableStatusMapper.toDTO(table, reservations, orders);
                logger.info("DTO created: " + dto);
                result.add(dto);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.severe("Error while fetching table statuses: " + e.getMessage());
        }

        return result;
    }

    /**
     * Cập nhật trạng thái của một bàn.
     */
    public boolean updateTableStatus(int tableId, TableBooking.StatusTable newStatus) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            TableBooking table = session.get(TableBooking.class, tableId);
            if (table != null) {
                table.setStatus(newStatus);
                session.update(table);

                // ✅ Đưa xử lý reservation vào đây
                if (newStatus == TableBooking.StatusTable.DA_DAT || newStatus == TableBooking.StatusTable.DANG_SU_DUNG) {
                    Reservation reservation = ReservationDAO.getInstance().findReservationByIdTable(tableId);
                    System.out.println("reservation: " + reservation);
                    if (reservation != null) {
                        reservation.setStatus(Reservation.ReservationStatus.DA_XAC_NHAN);
                        ReservationDAO.getInstance().update(reservation);
                    }
                }

                tx.commit(); // commit sau khi xử lý cả table lẫn reservation
                return true;
            }

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.severe("Error updating table status: " + e.getMessage());
        }

        return false;
    }


    /**
     * Cập nhật trạng thái của một đặt chỗ.
     */
    public boolean updateReservationStatus(int reservationId, Reservation.ReservationStatus newStatus) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Reservation reservation = session.get(Reservation.class, reservationId);
            if (reservation != null) {
                reservation.setStatus(newStatus);
                session.update(reservation);
                tx.commit();
                return true;
            }

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.severe("Error updating reservation status: " + e.getMessage());
        }

        return false;
    }

    /**
     * Cập nhật trạng thái của một đơn hàng.
     */
    public boolean updateOrderStatus(int orderId, Order.OrderStatus newStatus) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Order order = session.get(Order.class, orderId);
            if (order != null) {
                order.setStatus(newStatus);
                session.update(order);
                tx.commit();
                return true;
            }

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.severe("Error updating order status: " + e.getMessage());
        }

        return false;
    }

    // ============================
    // Private helper methods
    // ============================

    /**
     * Lấy danh sách reservation còn hiệu lực (≠ HUY) của một bàn.
     */
    private List<Reservation> getActiveReservationsByTable(Session session, int tableId) {
        Query<Reservation> query = session.createQuery(
                "from Reservation r where r.tableBooking.id = :tableId and r.status != :huy", Reservation.class);
        query.setParameter("tableId", tableId);
        query.setParameter("huy", Reservation.ReservationStatus.HUY);
        return query.list();
    }

    /**
     * Lấy danh sách đơn hàng còn hoạt động (≠ HOÀN THÀNH hoặc ĐÃ HỦY) của một bàn.
     */
    private List<Order> getActiveOrdersByTable(Session session, int tableId) {
        Query<Order> query = session.createQuery(
                "from Order o where o.table.id = :tableId and o.status not in (:excluded)", Order.class);
        query.setParameter("tableId", tableId);
        query.setParameterList("excluded", List.of(Order.OrderStatus.HOAN_THANH, Order.OrderStatus.DA_HUY));
        return query.list();
    }
}
