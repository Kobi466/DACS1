package dao;

import model.Customer;
import model.Order;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class OrderDAO extends AbstractDAO<Order, Integer> {
    public static OrderDAO getInstance(){
        return new OrderDAO();
    }
    public OrderDAO() {
        super(Order.class);
    }
    public List<Order> findByCustomerId(Customer cusTomer) {
        return em.createQuery("from Order where customer =: cusTomer", Order.class).setParameter("cusTomer", cusTomer).getResultList();
    }
    public List<Order> findCho_Xac_NhanOrders() {
        return em.createQuery("FROM Order o WHERE o.status = :status", Order.class)
                .setParameter("status", Order.OrderStatus.CHO_XAC_NHAN)
                .getResultList();
    }

    public void updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        try {
            em.getTransaction().begin();

            Order order = em.createQuery(
                            "FROM Order o WHERE o.order_Id = :orderId",
                            Order.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();

            order.setStatus(newStatus);
            em.merge(order);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Không thể cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
    }
    public List<Order> findNewOrders() {
        try {
            // Lấy thời điểm 5 phút trước
            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

            return em.createQuery(
                            "FROM Order o WHERE o.status = :status AND o.orderDate >= :fromTime",
                            Order.class)
                    .setParameter("status", Order.OrderStatus.CHO_XAC_NHAN)
                    .setParameter("fromTime", fiveMinutesAgo)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
