package repositoy_dao;

import model.Order;

public class OrderDAO extends AbstractDAO<Order, Integer> {
    public static OrderDAO getInstance() {
        return new OrderDAO();
    }
    public OrderDAO() {
        super(Order.class);
    }
}
