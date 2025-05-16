package view;

import controller.TableController;
import dto.TableStatusDTO;
import service.TableService;
import util.RoundedButtonUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class TablePanel extends JPanel {

    private final TableController controller;
    private final JPanel tableContainer;
    private final JComboBox<String> statusFilter;
    private List<TableStatusDTO> currentTables = new ArrayList<>();

    public TablePanel() {
        this.controller = new TableController(new TableService("localhost", 8080), this);
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // 🔹 Top filter panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        topPanel.setBackground(Color.WHITE);

        statusFilter = new JComboBox<>(new String[]{
                        "Tất cả", "TRONG", "CHO_XAC_NHAN", "DA_DAT", "DANG_SU_DUNG"
        });
        statusFilter.setPreferredSize(new Dimension(180, 30));
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 14));
        statusFilter.addActionListener(e -> renderTablesWithFilter());

        JLabel filterLabel = new JLabel("Lọc trạng thái:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(filterLabel);
        topPanel.add(statusFilter);

        this.add(topPanel, BorderLayout.NORTH);

        // 🔹 Main table display
        tableContainer = new JPanel(new GridLayout(3, 4, 20, 20));
        tableContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tableContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tableContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane, BorderLayout.CENTER);

        loadTables();
    }

    private void loadTables() {
        controller.fetchAllTableStatuses(tables -> {
            this.currentTables = tables;
            SwingUtilities.invokeLater(this::renderTablesWithFilter);
        });
    }

    private void renderTablesWithFilter() {
        tableContainer.removeAll();
        String selected = (String) statusFilter.getSelectedItem();

        List<TableStatusDTO> filtered = currentTables.stream()
                .filter(t -> selected.equals("Tất cả") || t.getStatus().name().equals(selected))
                .collect(Collectors.toList());

        for (TableStatusDTO dto : filtered) {
            tableContainer.add(createTableButton(dto));
        }

        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private JButton createTableButton(TableStatusDTO dto) {
        String label = "<html><center><b>" + dto.getTableName() + "</b><br/>"
                + (dto.getCustomerName() != null ? dto.getCustomerName() : "-") + "<br/>"
                + (dto.getReservationTime() != null ? dto.getReservationTime() : "-") + "<br/>"
                + (dto.getOrderStatus() != null ? dto.getOrderStatus().name() : "")
                + "</center></html>";

        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setPreferredSize(new Dimension(140, 100));
        btn.setBackground(getColorByStatus(dto.getStatus()));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setToolTipText(dto.getTooltipText());

        if (dto.getTableType() == TableStatusDTO.TableType.BAN) {
            btn.setUI(new RoundedButtonUI()); // bàn thường: bo tròn
        }

        btn.setBorder(BorderFactory.createLineBorder(dto.getTableType() == TableStatusDTO.TableType.PHONG_VIP
                ? new Color(255, 153, 0) : Color.LIGHT_GRAY, 3));

        // Hover hiệu ứng
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setBackground(btn.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(getColorByStatus(dto.getStatus()));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showDetailDialog(dto);
            }
        });

        return btn;
    }

    private void showDetailDialog(TableStatusDTO dto) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel[] labels = {
                new JLabel("Tên bàn:"), new JLabel(dto.getTableName()),
                new JLabel("Loại:"), new JLabel(dto.getTableType().name()),
                new JLabel("Trạng thái:"), new JLabel(dto.getStatus().name()),
                new JLabel("Khách hàng:"), new JLabel(dto.getCustomerName() != null ? dto.getCustomerName() : "-"),
                new JLabel("Giờ đặt:"), new JLabel(String.valueOf(dto.getReservationTime() != null ? dto.getReservationTime() : "-")),
                new JLabel("Đơn hàng:"), new JLabel(dto.getOrderStatus() != null ? dto.getOrderStatus().name() : "-")
        };

        for (JLabel label : labels) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(label);
        }

        // ComboBox đổi trạng thái
        JComboBox<TableStatusDTO.StatusTable> statusBox = new JComboBox<>(TableStatusDTO.StatusTable.values());
        statusBox.setSelectedItem(dto.getStatus());
        panel.add(new JLabel("Chuyển trạng thái:"));
        panel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thông tin chi tiết bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            TableStatusDTO.StatusTable selected = (TableStatusDTO.StatusTable) statusBox.getSelectedItem();
            if (selected != null && selected != dto.getStatus()) {
                controller.updateTableStatus(dto.getTableId(), selected);
            }
        }
    }

    private Color getColorByStatus(TableStatusDTO.StatusTable status) {
        return switch (status) {
            case TRONG -> new Color(0x4CAF50);           // Xanh lá
            case CHO_XAC_NHAN -> new Color(0xFFC107);    // Vàng
            case DA_DAT -> new Color(0x2196F3);          // Xanh dương
            case DANG_SU_DUNG -> new Color(0xE53935);    // Đỏ
            default -> Color.GRAY;
        };
    }

    public void updateTableStatuses(List<TableStatusDTO> updated) {
        this.currentTables = updated;
        SwingUtilities.invokeLater(this::renderTablesWithFilter);
    }
}