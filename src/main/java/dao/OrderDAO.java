package dao;

import model.Customer;
import model.Order;

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
}
