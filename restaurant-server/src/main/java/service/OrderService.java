package service;



import dto.OrderDTO;
import dto.OrderItemDTO;
import dto.OrderSummaryDTO;
import model.Order;
import model.OrderItem;
import repositoy_dao.OrderDAO;
import repositoy_dao.OrderItemDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    // 1. Lấy danh sách đơn hàng dạng tóm tắt
    public List<OrderSummaryDTO> getAllOrderSummaries() {
        List<Order> orders = orderDAO.selectAll();

        return orders.stream()
                .map(order -> new OrderSummaryDTO(
                        order.getOrder_Id(),
                        order.getCustomer().getUserName(),
                        order.getCustomer().getSdt(),
                        order.getOrderDate(),
                        OrderSummaryDTO.OrderStatus.valueOf(order.getStatus().name())
                ))
                .collect(Collectors.toList());
    }

    // 2. Lấy chi tiết đơn hàng theo ID
    public OrderDTO getOrderDetail(int orderId) {
        Order order = orderDAO.selecById(orderId);
        if (order == null) {
            System.out.println("[ERROR] Không tìm thấy đơn hàng ID: " + orderId);
            return null;
        }

        List<OrderItem> orderItems = orderItemDAO.findByOrderId(orderId);
        if (orderItems == null || orderItems.isEmpty()) {
            System.out.println("[WARN] Không có món ăn nào cho đơn hàng ID: " + orderId);
        } else {
            System.out.println("[DEBUG] Số lượng món ăn tìm được: " + orderItems.size());
        }

        List<OrderItemDTO> itemDTOs = orderItems.stream()
                .filter(item -> {
                    if (item.getMenuItem() == null) {
                        System.out.println("[ERROR] Món ăn null trong OrderItem ID: " + item.getOrderId());
                        return false;
                    }
                    return true;
                })
                .map(item -> new OrderItemDTO(
                        item.getMenuItem().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());

        double total = itemDTOs.stream()
                .map(OrderItemDTO::getTotalPrice)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        return new OrderDTO(
                order.getCustomer().getUserName(),
                order.getCustomer().getSdt(),
                order.getOrderDate(),
                total,
                itemDTOs
        );
    }




    // 3. Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(int orderId, Order.OrderStatus status) {
        orderDAO.updateStatus(orderId, status);
    }
}
