package service;

import dto.TableStatusDTO;
import model.MenuItem;
import model.Order;
import model.Reservation;
import model.TableBooking;
import repositoy_dao.MenuItemDAO;
import repositoy_dao.TableBookingDAO;
import repositoy_dao.TableStatusDAO;

import java.util.List;
import java.util.stream.Collectors;

public class TableStatusService {

    private final TableStatusDAO tableStatusDAO;

    public TableStatusService() {
        this.tableStatusDAO = new TableStatusDAO();
    }

    /**
     * Trả về danh sách trạng thái các bàn (bao gồm bàn, đơn hàng, đặt chỗ).
     */
    public List<TableStatusDTO> getAllTableStatuses() {
        return tableStatusDAO.getAllTableStatus();
    }

    /**
     * Cập nhật trạng thái bàn.
     */
    public boolean updateTableStatus(int tableId, TableBooking.StatusTable newStatus) {
        return tableStatusDAO.updateTableStatus(tableId, newStatus);
    }

    /**
     * Cập nhật trạng thái đặt chỗ.
     */
    public boolean updateReservationStatus(int reservationId, Reservation.ReservationStatus newStatus) {
        return tableStatusDAO.updateReservationStatus(reservationId, newStatus);
    }

    /**
     * Cập nhật trạng thái đơn hàng.
     */
    public boolean updateOrderStatus(int orderId, Order.OrderStatus newStatus) {
        return tableStatusDAO.updateOrderStatus(orderId, newStatus);
    }
    public List<String> showTableTrong(){
        List<TableBooking> tableBookings = TableBookingDAO.getInstance().findAllAvailable();
        List<String> tableList = tableBookings.stream()
                .map(table -> "- " + table.getTableName() + ": " + table.getStatus())
                .collect(Collectors.toList());
        return tableList;
    }
}
