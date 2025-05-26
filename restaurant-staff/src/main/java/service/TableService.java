package service;

import com.fasterxml.jackson.core.type.TypeReference;
import controller.TableController;
import dto.TableStatusDTO;
import network.GlobalResponseRouter;
import network.JsonRequest;
import network.JsonResponse;

import socket.SocketClient;
import util.JacksonUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TableService {
    public  String host;
    public  int port;

    public List<Consumer<List<TableStatusDTO>>> listeners = new ArrayList<>();

    public TableService(String host, int port) {
        this.host = host;
        this.port = port;
        SocketClient.connect(host, port);
        registerResponseHandlers();
    }

    public void fetchAllTableStatuses(Consumer<List<TableStatusDTO>> callback) {
        listeners.add(callback);
        JsonRequest request = new JsonRequest("GET_ALL_TABLE_STATUS", null);
        SocketClient.sendRequest(request, host, port);
    }

    public void updateTableStatus(int tableId, TableStatusDTO.StatusTable newStatus) {
        Map<String, Object> data = new HashMap<>();
        data.put("tableId", tableId);
        data.put("newStatus", newStatus.name());
        System.out.println("🔥 Sending UPDATE_TABLE_STATUS with data = " + data);
        JsonRequest request = new JsonRequest("UPDATE_TABLE_STATUS", data);
        SocketClient.sendRequest(request, host, port);
    }

    private void registerResponseHandlers() {
        GlobalResponseRouter.addListener((JsonResponse response) -> {
            try {
                String status = response.getStatus();
                if (status == null) return;

                switch (status) {
                    case "GET_ALL_TABLE_STATUS_SUCCESS" -> {
                        List<TableStatusDTO> list = JacksonUtils.getObjectMapper().convertValue(
                                response.getData(), new TypeReference<List<TableStatusDTO>>() {}
                        );
                        for (Consumer<List<TableStatusDTO>> listener : listeners) {
                            SwingUtilities.invokeLater(() -> listener.accept(list));
                        }
                        listeners.clear();
                    }
                    case "NEW_ORDER_CREATED" -> TableController.reloadTableStatus();
                    case "UPDATE_ORDER_STATUS_SUCCESS" -> {
                        // Server đã cập nhật, client có thể reload lại nếu muốn
                        TableController.reloadTableStatus();
                    }
                    case "UPDATE_TABLE_STATUS" -> {
                        TableController.reloadTableStatus(); // xử lý khi server chủ động thông báo
                    }
                    case "UPDATE_TABLE_STATUS_FAIL" -> {
                        JOptionPane.showMessageDialog(null,
                                "Cập nhật trạng thái bàn thất bại!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    case "UPDATE_TABLE_STATUS_SUCCESS" -> {
                        // Server đã cập nhật, client có thể reload lại nếu muốn
                        TableController.reloadTableStatus();
                    }

                    default -> {
                        System.out.println("⚠️ Không hiểu response: " + status);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
