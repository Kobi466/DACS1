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
    private final TableService tableService;

    public TablePanel() {
        this.tableService = new TableService("localhost", 8080);
        this.controller = new TableController(tableService);

        this.setLayout(new BorderLayout());

        // Bộ lọc trạng thái
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusFilter = new JComboBox<>(new String[]{
                "Tất cả", "TRONG", "CHO_XAC_NHAN", "DA_DAT", "DANG_SU_DUNG"
        });
        statusFilter.addActionListener(e -> renderTablesWithFilter());
        topPanel.add(new JLabel("Lọc trạng thái:"));
        topPanel.add(statusFilter);
        this.add(topPanel, BorderLayout.NORTH);

        // Khu vực hiển thị bàn
        tableContainer = new JPanel();
        tableContainer.setLayout(new GridLayout(3, 4, 20, 20));
        tableContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.add(new JScrollPane(tableContainer), BorderLayout.CENTER);

        loadTables();
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
    private void loadTables() {
        controller.fetchAllTableStatuses(tables -> {
            System.out.println("[DEBUG] Nhận được " + tables.size() + " bàn");
            this.currentTables = tables;
            SwingUtilities.invokeLater(this::renderTablesWithFilter);
        });
    }


    private JComponent createTableButton(TableStatusDTO dto) {
        JButton btn = new JButton("<html><center>" + dto.getTableName() + "<br/>"
                + (dto.getCustomerName() != null ? dto.getCustomerName() : "Khách: -") + "<br/>"
                + (dto.getReservationTime() != null ? dto.getReservationTime() : "") + "<br/>"
                + (dto.getOrderStatus() != null ? dto.getOrderStatus().name() : "")
                + "</center></html>");

        btn.setPreferredSize(new Dimension(120, 100));
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBackground(getColorByStatus(dto.getStatus()));
        btn.setOpaque(true);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setToolTipText(dto.getTooltipText());

        if (dto.getTableType() == TableStatusDTO.TableType.PHONG_VIP) {
            btn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
        } else {
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }

        // Bo tròn nếu là bàn thường
        if (dto.getTableType() == TableStatusDTO.TableType.BAN) {
            btn.setUI(new RoundedButtonUI());
        }

        // Click mở chi tiết
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDetailDialog(dto);
            }
        });

        return btn;
    }

    private Color getColorByStatus(TableStatusDTO.StatusTable status) {
        return switch (status) {
            case TRONG -> new Color(0x4CAF50);           // Xanh lá
            case CHO_XAC_NHAN -> new Color(0xFFC107);    // Vàng
            case DA_DAT -> new Color(0x2196F3);     // Xanh dương
            default -> Color.GRAY;
        };
    }

    private void showDetailDialog(TableStatusDTO dto) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 5));

        panel.add(new JLabel("Tên bàn: " + dto.getTableName()));
        panel.add(new JLabel("Loại: " + dto.getTableType()));
        panel.add(new JLabel("Trạng thái: " + dto.getStatus()));
        panel.add(new JLabel("Khách hàng: " + (dto.getCustomerName() != null ? dto.getCustomerName() : "Không có")));
        panel.add(new JLabel("Giờ đặt: " + (dto.getReservationTime() != null ? dto.getReservationTime() : "Không có")));
        panel.add(new JLabel("Đơn hàng: " + (dto.getOrderStatus() != null ? dto.getOrderStatus() : "Không có")));

        JComboBox<TableStatusDTO.StatusTable> statusBox = new JComboBox<>(TableStatusDTO.StatusTable.values());
        statusBox.setSelectedItem(dto.getStatus());
        panel.add(new JLabel("Thay đổi trạng thái:"));
        panel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Chi tiết bàn", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            TableStatusDTO.StatusTable selected = (TableStatusDTO.StatusTable) statusBox.getSelectedItem();
            if (selected != null && selected != dto.getStatus()) {
                controller.updateTableStatus(dto.getTableId(), selected);
            }
        }
    }
    public void updateTableStatuses(List<TableStatusDTO> updated) {
        this.currentTables = updated;
        SwingUtilities.invokeLater(this::renderTablesWithFilter); // Vẽ lại nếu có thay đổi
    }


}