package service;

public class OrderItemService extends AbstractService<model.OrderItem, Integer> {
    public OrderItemService() {
        this.dao = new repositoy_dao.OrderItemDAO();
    }
}
