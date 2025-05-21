package controller;

import dto.TableStatusDTO;
import service.TableService;
import view.TablePanel;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.stream.Collectors;

public class TableController {

    private final TableService service;
    private final TablePanel view;
    private List<TableStatusDTO> currentTables;

    public TableController(TableService service, TablePanel view) {
        this.service = service;
        this.view = view;
    }

    public void loadTables() {
        service.fetchAllTableStatuses(tables -> {
            this.currentTables = tables;
            SwingUtilities.invokeLater(() -> renderTablesWithFilter("Tất cả"));
        });
    }

    public void onFilterChanged(String selectedFilter) {
        renderTablesWithFilter(selectedFilter);
    }

    private void renderTablesWithFilter(String selectedFilter) {
        List<TableStatusDTO> filtered = currentTables.stream()
                .filter(t -> selectedFilter.equals("Tất cả") || t.getStatus().name().equals(selectedFilter))
                .collect(Collectors.toList());
        view.renderTables(filtered);
    }

    public void showTableDetailDialog(TableStatusDTO dto) {
        view.showDetailDialog(dto);
    }

    public void updateTableStatus(int tableId, TableStatusDTO.StatusTable status) {
        service.updateTableStatus(tableId, status, updatedTables -> {
            this.currentTables = updatedTables;
            SwingUtilities.invokeLater(() -> renderTablesWithFilter("Tất cả"));
        });
    }
}