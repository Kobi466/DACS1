package controller;

import dto.OrderDTO;
import dto.OrderSummaryDTO;
import network.GlobalResponseRouter;
import service.OrderService;
import view.OrderPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private final OrderPanel view;
    private final OrderService service;
    private final List<OrderSummaryDTO> orderList;


    public OrderController(OrderPanel view) {
        this.view = view;
        this.service = new OrderService();
        this.orderList = new ArrayList<>();
    }

    // ===================== LOAD ĐƠN HÀNG =====================

    public void loadOrders() {
        service.fetchOrders(
                orders -> {
                    SwingUtilities.invokeLater(() -> {
                        setOrders(orders);
                        view.updateOrderTable(orderList);
                    });
                },
                view::showError
        );
    }


    public void reloadOrders() {
        loadOrders();
    }

    // ===================== CHI TIẾT ĐƠN =====================

    public void loadOrderDetail(int orderId) {
        service.fetchOrderDetail(
                orderId,
                order -> SwingUtilities.invokeLater(() -> view.updateOrderDetail(order)),
                view::showError
        );
    }

    // ===================== CẬP NHẬT TRẠNG THÁI =====================

    public void updateOrderStatus(int orderId, OrderSummaryDTO.OrderStatus newStatus) {
        service.updateOrderStatus(
                orderId,
                newStatus,
                () -> SwingUtilities.invokeLater(() -> {
                    updateLocalOrderStatus(orderId, newStatus);
                    view.updateOrderTable(orderList);
                    view.showMessage("✅ Cập nhật trạng thái thành công.");
                }),
                errorMessage -> SwingUtilities.invokeLater(() -> view.showError("❌ " + errorMessage))
        );
    }

    private void updateLocalOrderStatus(int orderId, OrderSummaryDTO.OrderStatus newStatus) {
        for (OrderSummaryDTO order : orderList) {
            if (order.getOrderId() == orderId) {
                order.setStatus(newStatus);
                break;
            }
        }
    }

    // ===================== QUẢN LÝ DANH SÁCH =====================

    public void addOrder(OrderSummaryDTO newOrder) {
        orderList.add(0, newOrder); // thêm đầu danh sách
    }

    public OrderSummaryDTO getOrderById(int orderId) {
        for (OrderSummaryDTO order : orderList) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }

    public List<OrderSummaryDTO> getOrders() {
        return new ArrayList<>(orderList); // trả về bản sao để tránh sửa ngoài
    }

    public void setOrders(List<OrderSummaryDTO> orders) {
        orderList.clear();
        if (orders != null) {
            orderList.addAll(orders);
        }
    }
}
