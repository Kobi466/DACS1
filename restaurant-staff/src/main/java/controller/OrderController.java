package controller;

import dto.OrderSummaryDTO;
import service.OrderService;
import view.OrderPanel;

import java.util.List;

public class OrderController {
    private final OrderService orderService;
    private static OrderPanel orderPanel = null;

    public OrderController(OrderPanel panel) {
        this.orderPanel = panel;
        this.orderService = new OrderService();
    }

    public void loadOrders() {
        orderService.fetchOrders(
                this::onOrdersLoaded,
                orderPanel::showError
        );
    }

    private void onOrdersLoaded(List<OrderSummaryDTO> orders) {
        orderPanel.updateOrderTable(orders);
    }
}
