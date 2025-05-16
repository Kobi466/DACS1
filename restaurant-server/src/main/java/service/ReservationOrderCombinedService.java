package service;

import dto.OrderDTO;
import dto.OrderItemDTO;
import dto.ReservationOrderDTO;
import mapper.ReservationOrderMapper;
import model.Customer;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.Reservation;
import model.TableBooking;
import org.springframework.stereotype.Service;
import repositoy_dao.CustomerDAO;
import repositoy_dao.MenuItemDAO;
import repositoy_dao.OrderDAO;
import repositoy_dao.ReservationDAO;
import repositoy_dao.TableBookingDAO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dịch vụ kết hợp xử lý đặt bàn và tạo order, trả về chi tiết OrderDTO.
 */
@Service
public class ReservationOrderCombinedService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final TableBookingDAO tableBookingDAO = new TableBookingDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final MenuItemDAO menuItemDAO = new MenuItemDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ReservationOrderMapper mapper = new ReservationOrderMapper();

    /**
     * Xử lý đặt bàn và tạo order.
     * @param dto dữ liệu đặt bàn + order từ client
     * @return OrderDTO chứa chi tiết order vừa tạo
     */
    public OrderDTO processReservationOrder(ReservationOrderDTO dto) {
        // 1. Lấy thông tin khách hàng
        Customer customer = customerDAO.selecById(dto.getId());
        if (customer == null) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + dto.getId());
        }

        // 2. Tìm bàn trống theo mã
        TableBooking table = tableBookingDAO.findAvailableByCode(
                dto.getTableCode(), TableBooking.StatusTable.TRONG);
        if (table == null) {
            throw new RuntimeException("Không tìm thấy bàn trống với mã: " + dto.getTableCode());
        }

        // 3. Cập nhật trạng thái bàn
        table.setStatus(TableBooking.StatusTable.CHO_XAC_NHAN);
        tableBookingDAO.update(table);

        // 4. Lưu reservation
        Reservation reservation = mapper.toReservation(dto, customer, table);
        reservationDAO.insert(reservation);

        // 5. Tạo Order và set thông tin cơ bản
        Order order = new Order();
        order.setCustomer(customer);
        order.setTable(table);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.CHO_XAC_NHAN);

        // 6. Tạo danh sách OrderItem
        List<OrderItem> items = dto.getItems().stream()
                .map(req -> {
                    MenuItem menuItem = menuItemDAO.findByName(req.getItemName());
                    if (menuItem == null) {
                        throw new RuntimeException("Không tìm thấy món: " + req.getItemName());
                    }
                    OrderItem oi = new OrderItem();
                    oi.setOrder(order);
                    oi.setMenuItem(menuItem);
                    oi.setQuantity(req.getQuantity());
                    oi.setPrice(menuItem.getPrice());
                    return oi;
                })
                .collect(Collectors.toList());

        // 7. Gắn OrderItem vào Order và lưu (cascade)
        order.setOrderItems(items);
        orderDAO.insert(order);

        // 8. Build và trả về OrderDTO
        OrderDTO result = new OrderDTO();
        result.setOrderId(order.getOrder_Id());
        result.setCustomerName(customer.getUserName());
        result.setCustomerPhone(customer.getSdt());
        result.setOrderDate(order.getOrderDate());

        // Map chi tiết món ăn
        List<OrderItemDTO> itemDTOs = items.stream().map(item -> {
            OrderItemDTO dtoItem = new OrderItemDTO();
            dtoItem.setFoodName(item.getMenuItem().getName());
            dtoItem.setQuantity(item.getQuantity());
            dtoItem.setUnitPrice(item.getPrice());
            dtoItem.setTotalPrice(
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
            return dtoItem;
        }).collect(Collectors.toList());
        result.setItems(itemDTOs);

        // Tính tổng tiền đơn
        BigDecimal total = itemDTOs.stream()
                .map(OrderItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.setTotalPrice(total.doubleValue());

        return result;
    }
}
