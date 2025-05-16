package repositoy_dao;

import model.Order;
import model.TableBooking;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class OrderDAO extends AbstractDAO<Order, Integer> {
    public static OrderDAO getInstance() {
        return new OrderDAO();
    }
    public OrderDAO() {
        super(Order.class);
    }
    public void updateStatus(int orderId, Order.OrderStatus newStatus) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Order order = session.get(Order.class, orderId);
            if (order != null) {
                order.setStatus(newStatus);
                session.update(order);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
    public TableBooking findTableByOrderId(int orderId) {
        Transaction tx = null;
        TableBooking tableBooking = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Truy vấn lấy TableBooking từ đơn hàng
            String hql = "SELECT o.table FROM Order o WHERE o.id = :orderId";
            tableBooking = session.createQuery(hql, TableBooking.class)
                    .setParameter("orderId", orderId)
                    .uniqueResult();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }

        return tableBooking;
    }

}
