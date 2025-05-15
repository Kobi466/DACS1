package controller;

import dto.TableStatusDTO;
import network.JsonRequest;
import service.TableService;
import socket.SocketClient;
import view.TablePanel;

import java.util.List;
import java.util.function.Consumer;

public class TableController {

    private static TableService tableService;
    private static final TablePanel view = new TablePanel();

    public TableController(TableService tableService) {
        TableController.tableService = tableService;
    }

    public void fetchAllTableStatuses(Consumer<List<TableStatusDTO>> callback) {
        this.tableService.listeners.add(callback);
        JsonRequest request = new JsonRequest("GET_ALL_TABLE_STATUS", null);
        SocketClient.sendRequest(request, "localhost", 8080);
    }

    public static void reloadTableStatus() {
        tableService.fetchAllTableStatuses(view::updateTableStatuses);
    }

    public void updateTableStatus(int tableId, TableStatusDTO.StatusTable newStatus) {
        tableService.updateTableStatus(tableId, newStatus);
    }
}
