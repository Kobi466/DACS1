package service;

import dto.ReservationOrderDTO;
import mapper.ReservationOrderMapper;
import model.*;

import org.springframework.stereotype.Service;
import repositoy_dao.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationOrderCombinedService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final TableBookingDAO tableBookingDAO = new TableBookingDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final MenuItemDAO menuItemDAO = new MenuItemDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ReservationOrderMapper mapper = new ReservationOrderMapper();

    public void processReservationOrder(ReservationOrderDTO dto) {
        // 1. Lấy thông tin khách hàng
        Customer customer = customerDAO.selecById(dto.getId());
        if (customer == null) {
            throw new RuntimeException("Không tìm thấy khách hàng!");
        }

        // 2. Tìm bàn trống theo mã
        TableBooking table = tableBookingDAO.findAvailableByCode(dto.getTableCode(), TableBooking.StatusTable.TRONG);
        if (table == null) {
            throw new RuntimeException("Không tìm thấy bàn trống với mã: " + dto.getTableCode());
        }

        // 3. Đặt trạng thái bàn là ĐÃ ĐẶT
        table.setStatus(TableBooking.StatusTable.DA_DAT);
        tableBookingDAO.update(table);

        // 4. Tạo và lưu reservation
        Reservation reservation = mapper.toReservation(dto, customer, table);
        reservationDAO.insert(reservation);

        // 5. Tạo Order trước
        Order order = new Order();
        order.setCustomer(customer);
        order.setTable(table);
        order.setOrderDate(LocalDateTime.now());

        // 6. Tạo danh sách OrderItem gắn với Order
        List<OrderItem> items = new ArrayList<>();
        for (ReservationOrderDTO.ItemRequest itemDTO : dto.getItems()) {
            MenuItem menuItem = menuItemDAO.findByName(itemDTO.getItemName());
            if (menuItem == null) continue;

            OrderItem item = new OrderItem();
            item.setOrder(order); // Gắn Order cha
            item.setMenuItem(menuItem);
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(menuItem.getPrice());

            items.add(item);
        }

        // 7. Gắn danh sách món vào Order và lưu
        order.setOrderItems(items);
        order.setStatus(Order.OrderStatus.CHO_XAC_NHAN); // Hoặc trạng thái khác nếu cần
        orderDAO.insert(order); // cascade sẽ lưu luôn OrderItem

        // ✅ Xong toàn bộ quá trình
    }
}
