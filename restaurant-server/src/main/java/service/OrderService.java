package service;


import controller.MessageController;
import dto.OrderDTO;
import dto.OrderItemDTO;
import dto.OrderSummaryDTO;
import model.Order;
import model.OrderItem;
import model.Reservation;
import model.TableBooking;
import repositoy_dao.OrderDAO;
import repositoy_dao.OrderItemDAO;
import repositoy_dao.ReservationDAO;
import repositoy_dao.TableBookingDAO;

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

    public boolean updateOrderStatus(int orderId, OrderSummaryDTO.OrderStatus statusDTO) {
        Order order = orderDAO.selecById(orderId);
        if (order == null) {
            System.err.println("[ERROR] Không tìm thấy đơn hàng với ID: " + orderId);
            return false;
        }

        Order.OrderStatus current = order.getStatus();
        Order.OrderStatus next = Order.OrderStatus.valueOf(statusDTO.name());

        if (!isValidTransition(current, next)) {
            System.out.printf("[WARN] Không thể chuyển trạng thái từ %s ➜ %s (ID: %d)%n", current, next, orderId);
            return false;
        }

        order.setStatus(next);
        orderDAO.update(order);

        System.out.printf("[INFO] Đơn hàng #%d chuyển trạng thái: %s ➜ %s%n", orderId, current, next);

        // 👉 Hành động theo từng trạng thái
        switch (next) {
            case DA_XAC_NHAN:
                System.out.println("[ACTION] Xác nhận đơn hàng – gửi thông báo tới khách hàng.");
                MessageController.notifyCustomer(order, "Đơn hàng của bạn đã được xác nhận.");

                TableBooking table = OrderDAO.getInstance().findTableByOrderId(orderId);
                if (table != null) {
                    table.setStatus(TableBooking.StatusTable.DA_DAT);
                    TableBookingDAO.getInstance().update(table);
                    Reservation re = ReservationDAO.getInstance().findReservationByIdTable(table.getId());
                    if (re != null) {
                        re.setStatus(Reservation.ReservationStatus.DA_XAC_NHAN);
                        ReservationDAO.getInstance().update(re);
                    }
                }
                break;

            case DANG_CHE_BIEN:
                System.out.println("[ACTION] Bắt đầu chế biến đơn hàng.");
                MessageController.notifyCustomer(order, "Đơn hàng của bạn đã đang nhà bếp chế biến.");

                TableBooking table1 = OrderDAO.getInstance().findTableByOrderId(orderId);
                if (table1 != null) {
                    table1.setStatus(TableBooking.StatusTable.DANG_SU_DUNG);
                    TableBookingDAO.getInstance().update(table1);
                }
                break;

            case HOAN_THANH:
                System.out.println("[ACTION] Đơn hàng hoàn thành – xuất hóa đơn PDF.");
                MessageController.notifyCustomer(order, "Đơn hàng của bạn đã hoàn thành. Xin mời bạn thanh toán đơn hàng");

                // TODO: InvoiceService.generatePDF(order);
                TableBooking table2 = OrderDAO.getInstance().findTableByOrderId(orderId);
                if (table2 != null) {
                    table2.setStatus(TableBooking.StatusTable.TRONG);
                    TableBookingDAO.getInstance().update(table2);
                    Reservation re1 = ReservationDAO.getInstance().findReservationByIdTable2(table2.getId());
                    if (re1 != null) {
                        ReservationDAO.getInstance().delete(re1);
                    }
                }
                break;

            case DA_HUY:
                System.out.println("[ACTION] Đơn hàng bị hủy – gửi thông báo tới khách hàng.");
                MessageController.notifyCustomer(order, String.valueOf(next));
                TableBooking table3 = OrderDAO.getInstance().findTableByOrderId(orderId);
                if (table3 != null) {
                    table3.setStatus(TableBooking.StatusTable.TRONG);
                    TableBookingDAO.getInstance().update(table3);
                    Reservation re = ReservationDAO.getInstance().findReservationByIdTable(table3.getId());
                    if (re != null) {
                        ReservationDAO.getInstance().delete(re);
                    }
                }
                Order order1 = OrderDAO.getInstance().selecById(orderId);
                if (order1 != null) {
                    OrderDAO.getInstance().delete(order1);
                }
                break;
        }
        return true;
    }

    private boolean isValidTransition(Order.OrderStatus current, Order.OrderStatus next) {
        switch (current) {
            case CHO_XAC_NHAN:
                return next == Order.OrderStatus.DA_XAC_NHAN || next == Order.OrderStatus.DA_HUY;

            case DA_XAC_NHAN:
                return next == Order.OrderStatus.DANG_CHE_BIEN || next == Order.OrderStatus.DA_HUY;

            case DANG_CHE_BIEN:
                return next == Order.OrderStatus.HOAN_THANH || next == Order.OrderStatus.DA_HUY;

            // Các trạng thái kết thúc không được chuyển tiếp
            case HOAN_THANH:
            case DA_HUY:
                return false;

            default:
                return false;
        }
    }


}
