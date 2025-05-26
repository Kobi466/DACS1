package controller;

import dto.TableStatusDTO;
import network.JsonRequest;
import service.TableService;
import socket.SocketClient;
import view.TablePanel;

import java.util.List;
import java.util.function.Consumer;

public class TableController {
    private static TablePanel view;
    private static TableService tableService;

    public TableController(TableService tableService, TablePanel view) {
        this.tableService = tableService;
        this.view = view;
    }

    public void fetchAllTableStatuses(Consumer<List<TableStatusDTO>> callback) {
        tableService.listeners.add(callback);
        JsonRequest request = new JsonRequest("GET_ALL_TABLE_STATUS", null);
        SocketClient.sendRequest(request, tableService.host, tableService.port);
    }

    public static void reloadTableStatus() {
        tableService.fetchAllTableStatuses(view::updateTableStatuses);
    }

    public void updateTableStatus(int tableId, TableStatusDTO.StatusTable newStatus) {
        tableService.updateTableStatus(tableId, newStatus);
    }
}
