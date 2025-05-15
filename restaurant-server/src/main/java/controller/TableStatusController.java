package controller;

import dto.TableStatusDTO;
import model.Order;
import model.Reservation;
import model.TableBooking;
import network.JsonRequest;
import network.JsonResponse;
import service.TableStatusService;
import socketserver.ClientHandler;

import java.util.List;
import java.util.Map;

public class TableStatusController {

    private final TableStatusService service = new TableStatusService();

    public void handleGetAllTableStatus(JsonRequest request, ClientHandler handler) {
        List<TableStatusDTO> list = service.getAllTableStatuses();
        handler.sendResponse(new JsonResponse("GET_ALL_TABLE_STATUS_SUCCESS", list));
    }
    public void handleUpdateTableStatus(JsonRequest request, ClientHandler handler) {
        handleStatusUpdate(
                request, handler,
                "tableId", "newStatus",
                TableBooking.StatusTable.class,
                (id, status) -> service.updateTableStatus(id, (TableBooking.StatusTable) status),
                "UPDATE_TABLE_STATUS"
        );
    }

    public void handleUpdateReservationStatus(JsonRequest request, ClientHandler handler) {
        handleStatusUpdate(
                request, handler,
                "reservationId", "newStatus",
                Reservation.ReservationStatus.class,
                (id, status) -> service.updateReservationStatus(id, (Reservation.ReservationStatus) status),
                "UPDATE_RESERVATION_STATUS"
        );
    }

    public void handleUpdateOrderStatus(JsonRequest request, ClientHandler handler) {
        handleStatusUpdate(
                request, handler,
                "orderId", "newStatus",
                Order.OrderStatus.class,
                (id, status) -> service.updateOrderStatus(id, (Order.OrderStatus) status),
                "UPDATE_ORDER_STATUS_FROM_TABLE"
        );
    }

    // Generic xử lý cập nhật trạng thái
    private <T extends Enum<T>> void handleStatusUpdate(
            JsonRequest request,
            ClientHandler handler,
            String idKey,
            String statusKey,
            Class<T> statusEnum,
            StatusUpdater updater,
            String actionPrefix
    ) {
        try {
            Map<String, Object> data = (Map<String, Object>) request.getData();
            int id = ((Double) data.get(idKey)).intValue();
            String statusStr = (String) data.get(statusKey);

            T status = Enum.valueOf(statusEnum, statusStr);
            boolean result = updater.update(id, status);

            handler.sendResponse(new JsonResponse(actionPrefix + "_SUCCESS", result));
        } catch (Exception e) {
            handler.sendResponse(new JsonResponse(actionPrefix + "_FAIL", e.getMessage()));
        }
    }

    @FunctionalInterface
    private interface StatusUpdater {
        boolean update(int id, Enum<?> status);
    }
}
