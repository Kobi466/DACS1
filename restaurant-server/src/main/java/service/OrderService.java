package service;

public class OrderService extends AbstractService<model.Order, Integer> {
    public OrderService() {
        this.dao = new repositoy_dao.OrderDAO();
    }
}
