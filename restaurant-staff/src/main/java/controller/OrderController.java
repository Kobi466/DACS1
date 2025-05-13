package controller;

import dto.OrderDTO;
import dto.OrderSummaryDTO;
import service.OrderService;
import view.OrderPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private final OrderPanel view;
    private final OrderService service;
    private List<OrderSummaryDTO> orderList = new ArrayList<>();

    public OrderController(OrderPanel view) {
        this.view = view;
        this.service = new OrderService();
    }

    public void loadOrders() {
        service.fetchOrders(
                orders -> {
                    this.orderList = orders;
                    SwingUtilities.invokeLater(() -> view.updateOrderTable(orders));
                },
                view::showError
        );
    }

    public void reloadOrders() {
        loadOrders(); // cùng gọi loadOrders
    }

    public void loadOrderDetail(int orderId) {
        service.fetchOrderDetail(
                orderId,
                order -> SwingUtilities.invokeLater(() -> view.updateOrderDetail(order)),
                view::showError
        );
    }

    public void addOrder(OrderSummaryDTO newOrder) {
        this.orderList.add(newOrder);
    }

    public List<OrderSummaryDTO> getOrders() {
        return orderList;
    }

    public void setOrders(List<OrderSummaryDTO> orders) {
        this.orderList = orders;
    }
}
