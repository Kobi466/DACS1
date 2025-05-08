package repositoy_dao;

import model.OrderItem;

public class OrderItemDAO extends AbstractDAO<OrderItem, Integer> {
    public static OrderItemDAO getInstance() {
        return new OrderItemDAO();
    }

    public OrderItemDAO() {
        super(OrderItem.class);
    }
}
