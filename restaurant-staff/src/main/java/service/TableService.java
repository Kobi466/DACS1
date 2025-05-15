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

    public List<Consumer<List<TableStatusDTO>>> listeners = new ArrayList<>();

    public TableService(String host, int port) {
        SocketClient.connect(host, port);
        registerResponseHandlers();
    }

    public void fetchAllTableStatuses(Consumer<List<TableStatusDTO>> callback) {
        listeners.add(callback);
        JsonRequest request = new JsonRequest("GET_ALL_TABLE_STATUS", null);
        SocketClient.sendRequest(request, "localhost", 8080);
    }

    public void updateTableStatus(int tableId, TableStatusDTO.StatusTable newStatus) {
        Map<String, Object> data = new HashMap<>();
        data.put("tableId", tableId);
        data.put("status", newStatus.name());

        JsonRequest request = new JsonRequest("UPDATE_TABLE_STATUS", data);
        SocketClient.sendRequest(request, "localhost", 8080);
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

                    case "TABLE_STATUS_UPDATED" -> {
                        TableController.reloadTableStatus();
                    }

                    default -> {}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
