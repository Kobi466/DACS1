package mapper;

import dto.*;
import model.Order;
import model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    // 1. Tạo OrderSummaryDTO từ Order entity
    public static OrderSummaryDTO toOrderSummaryDTO(Order order) {
        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setOrderId(order.getOrder_Id());
        dto.setCustomerName(order.getCustomer().getUserName());
        dto.setCustomerPhone(order.getCustomer().getSdt());
        dto.setOrderDate(order.getOrderDate());

        double totalPrice = calculateTotalPrice(order.getOrderItems()); // tính tổng tiền
        dto.setTotalPrice(totalPrice);

        dto.setStatus(OrderSummaryDTO.OrderStatus.valueOf(order.getStatus().name()));
        return dto;
    }

    // 2. Tạo OrderDTO chi tiết từ Order entity
    public static OrderDTO toOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrder_Id());
        dto.setCustomerName(order.getCustomer().getUserName());
        dto.setCustomerPhone(order.getCustomer().getSdt());
        dto.setOrderDate(order.getOrderDate());

        List<OrderItemDTO> itemDTOs = mapOrderItems(order.getOrderItems());
        dto.setItems(itemDTOs);

        double totalPrice = itemDTOs.stream()
                .mapToDouble(i -> i.getTotalPrice().doubleValue())
                .sum();
        dto.setTotalPrice(totalPrice);

        return dto;
    }

    // 3. Helper: Convert list OrderItem → OrderItemDTO
    private static List<OrderItemDTO> mapOrderItems(List<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    BigDecimal unitPrice = item.getPrice(); // đơn giá
                    int quantity = item.getQuantity();
                    return new OrderItemDTO(
                            item.getMenuItem().getName(),                     // tên món lấy từ menuItem
                            quantity,
                            unitPrice,
                            unitPrice.multiply(BigDecimal.valueOf(quantity)) // tổng tiền = đơn giá × số lượng
                    );
                })
                .collect(Collectors.toList());
    }
    private static double calculateTotalPrice(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity())
                .sum();
    }
    public static OrderSummaryDTO fromReservationOrder(ReservationOrderDTO dto, CustomerDTO customer) {
        OrderSummaryDTO summary = new OrderSummaryDTO();
        summary.setOrderId(dto.getId());
        summary.setOrderDate(LocalDateTime.of(LocalDate.now(), dto.getBookingTime()));
        summary.setStatus(OrderSummaryDTO.OrderStatus.CHO_XAC_NHAN);
        summary.setCustomerName(customer.getUserName()); // hoặc getUsername() nếu có
        summary.setCustomerPhone(customer.getSdt());

        return summary;
    }


}
