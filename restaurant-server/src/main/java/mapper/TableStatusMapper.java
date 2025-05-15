package mapper;

import dto.OrderSummaryDTO;
import dto.TableStatusDTO;
import dto.TableStatusDTO.*;
import model.Order;
import model.Reservation;
import model.TableBooking;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TableStatusMapper {

    public static TableStatusDTO toDTO(TableBooking table, List<Reservation> reservations, List<Order> orders) {
        TableStatusDTO dto = new TableStatusDTO();

        dto.setTableId(table.getId());
        dto.setTableName(table.getTableName());
        dto.setTableType(TableType.valueOf(table.getTableType().name()));
        dto.setStatus(StatusTable.valueOf(table.getStatus().name()));

        // Reservation gần nhất (status khác HUY)
        Optional<Reservation> latestReservation = reservations.stream()
                .filter(r -> r.getStatus() != Reservation.ReservationStatus.HUY)
                .max(Comparator.comparing(Reservation::getBookingTime));

        latestReservation.ifPresent(res -> {
            dto.setReservationTime(res.getBookingTime());
            dto.setReservationStatus(ReservationStatus.valueOf(res.getStatus().name()));
            dto.setCustomerName(res.getCustomer().getUserName());
        });

        // Order gần nhất (trạng thái không phải DA_HUY hoặc HOAN_THANH)
        Optional<Order> latestOrder = orders.stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.DA_HUY && o.getStatus() != Order.OrderStatus.HOAN_THANH)
                .max(Comparator.comparing(Order::getOrderDate));

        latestOrder.ifPresent(order -> {
            dto.setOrderTime(order.getOrderDate());
            dto.setOrderStatus(OrderSummaryDTO.OrderStatus.valueOf(order.getStatus().name()));

            if (dto.getCustomerName() == null && order.getCustomer() != null) {
                dto.setCustomerName(order.getCustomer().getUserName());
            }
        });

        // Tổng số order đang hoạt động
        int activeCount = (int) orders.stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.DA_HUY && o.getStatus() != Order.OrderStatus.HOAN_THANH)
                .count();
        dto.setActiveOrderCount(activeCount);

        // Tooltip
        dto.setTooltipText(generateTooltip(dto));

        return dto;
    }

    private static String generateTooltip(TableStatusDTO dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Trạng thái: ").append(dto.getStatus()).append("\n");
        if (dto.getCustomerName() != null) {
            sb.append("Khách: ").append(dto.getCustomerName()).append("\n");
        }
        if (dto.getReservationTime() != null) {
            sb.append("Giờ đặt: ").append(dto.getReservationTime()).append("\n");
            sb.append("Tình trạng đặt: ").append(dto.getReservationStatus()).append("\n");
        }
        if (dto.getOrderTime() != null) {
            sb.append("Giờ gọi món: ").append(dto.getOrderTime()).append("\n");
            sb.append("Trạng thái đơn: ").append(dto.getOrderStatus()).append("\n");
        }
        sb.append("Số đơn đang hoạt động: ").append(dto.getActiveOrderCount());
        return sb.toString();
    }
}
