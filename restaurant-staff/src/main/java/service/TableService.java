package service;

import com.fasterxml.jackson.core.type.TypeReference;
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

    private final String host;
    private final int port;
    private final List<Consumer<List<TableStatusDTO>>> tableStatusListeners = new ArrayList<>();

    public TableService(String host, int port) {
        this.host = host;
        this.port = port;
        SocketClient.connect(host, port);
        registerResponseHandlers();
    }

    public void fetchAllTableStatuses(Consumer<List<TableStatusDTO>> callback) {
        tableStatusListeners.add(callback);
        JsonRequest request = new JsonRequest("GET_ALL_TABLE_STATUS", null);
        SocketClient.sendRequest(request, host, port);
    }

    public void updateTableStatus(int tableId, TableStatusDTO.StatusTable newStatus, Consumer<List<TableStatusDTO>> callback) {
        tableStatusListeners.add(callback);
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
                    case "GET_ALL_TABLE_STATUS_SUCCESS":
                    case "UPDATE_TABLE_STATUS_SUCCESS": {
                        Object data = response.getData(); // Lấy dữ liệu phản hồi
                        if (data instanceof List) {
                            // Chuyển đổi sang định dạng List<TableStatusDTO>
                            List<TableStatusDTO> list = JacksonUtils.getObjectMapper().convertValue(
                                    data, new TypeReference<List<TableStatusDTO>>() {}
                            );
                            // Gửi danh sách dữ liệu đến các listener
                            for (Consumer<List<TableStatusDTO>> listener : tableStatusListeners) {
                                SwingUtilities.invokeLater(() -> listener.accept(list));
                            }
                        } else {
                            System.err.println("⚠️ Warning: Dữ liệu phản hồi không phải List<TableStatusDTO>, nhận được: " + data);
                        }
                        break;
                    }
                    case "UPDATE_TABLE_STATUS_FAIL": {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                                null,
                                "Cập nhật trạng thái bàn thất bại!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE
                        ));
                        break;
                    }
                    default: {
                        System.out.println("⚠️ Không hiểu response: " + status);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}