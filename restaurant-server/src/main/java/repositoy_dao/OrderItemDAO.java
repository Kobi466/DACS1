package repositoy_dao;

import model.Order;
import model.OrderItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.util.List;

public class OrderItemDAO extends AbstractDAO<OrderItem, Integer> {
    public static OrderItemDAO getInstance() {
        return new OrderItemDAO();
    }

    public OrderItemDAO() {
        super(OrderItem.class);
    }

    public List<OrderItem> findByOrder(Order orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Query<OrderItem> query = session.createQuery("FROM OrderItem WHERE orderId = :orderId", OrderItem.class);
            query.setParameter("orderId", orderId);
            List<OrderItem> orderItems = query.list();
            transaction.commit();
            return orderItems;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<OrderItem> findByOrderId(int orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM OrderItem oi JOIN FETCH oi.menuItem WHERE oi.order.id = :orderId", OrderItem.class)
                    .setParameter("orderId", orderId)
                    .list();
        }
    }
}
