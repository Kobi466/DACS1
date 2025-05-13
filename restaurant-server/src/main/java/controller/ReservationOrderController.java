package controller;

import dto.OrderSummaryDTO;
import dto.ReservationOrderDTO;
import mapper.OrderMapper;
import network.JsonRequest;
import network.JsonResponse;
import service.ReservationOrderCombinedService;
import socketserver.ClientHandler;

import java.util.Map;

import static socketserver.ServerSocketHandler.clientMap;

public class ReservationOrderController {
    public void handleReserveAndOrder(JsonRequest request, ClientHandler clientHandler) {
        try {
            ReservationOrderDTO dto = (ReservationOrderDTO) request.getData();

            ReservationOrderCombinedService service = new ReservationOrderCombinedService();
            service.processReservationOrder(dto);

            // ✅ Gửi phản hồi thành công về cho client
            clientHandler.sendResponse(new JsonResponse("RESERVE_AND_ORDER_SUCCESS", "Đặt bàn & gọi món thành công"));

            // ✅ Gửi broadcast đơn hàng mới đến các staff
            // Chuyển đổi dto sang OrderSummaryDTO
            OrderSummaryDTO summary = OrderMapper.fromReservationOrder(dto,LoginController.customerDTO);
            // ✅ Gửi NEW_ORDER_CREATED tới toàn bộ staff
            for (Map.Entry<String, ClientHandler> entry : clientMap.entrySet()) {
                String username = entry.getKey();
                ClientHandler handler = entry.getValue();

                if (username.startsWith("staff")) { // tuỳ bạn quy ước
                    handler.sendResponse(new JsonResponse("NEW_ORDER_CREATED", summary));
                }
            }

            System.out.println("✅ Đã broadcast đơn hàng mới tới staff.");

        } catch (Exception e) {
            e.printStackTrace();
            clientHandler.sendResponse(new JsonResponse("RESERVE_AND_ORDER_FAILED", "❌ Thất bại: " + e.getMessage()));
        }
    }
}
