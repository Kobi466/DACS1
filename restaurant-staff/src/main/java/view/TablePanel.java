package view;

import controller.TableController;
import dto.TableStatusDTO;
import service.TableService;
import util.RoundedButtonUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TablePanel extends JPanel {

    private final TableController controller;
    private final JPanel tableContainer;
    private final JComboBox<String> statusFilter;
    private String host;
    private int port;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public TablePanel(String host, int port) {
        this.host = host;
        this.port = port;
        this.controller = new TableController(new TableService(host, port), this);
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(0xF5F7FA));

        // 🔹 Top filter panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(new Color(0xFFFFFF));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)));
        topPanel.setPreferredSize(new Dimension(0, 60));

        JLabel filterLabel = new JLabel("Lọc trạng thái:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterLabel.setForeground(new Color(0x333333));

        statusFilter = new JComboBox<>(new String[]{
                "Tất cả", "Trống", "Chờ xác nhận", "Đã đặt", "Đang sử dụng"
        });
        statusFilter.setPreferredSize(new Dimension(200, 35));
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setForeground(new Color(0x333333));
        statusFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        statusFilter.addActionListener(e -> controller.onFilterChanged((String) statusFilter.getSelectedItem()));

        topPanel.add(filterLabel);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(statusFilter);

        this.add(topPanel, BorderLayout.NORTH);

        // 🔹 Main table display
        tableContainer = new JPanel(new GridLayout(0, 4, 15, 15));
        tableContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tableContainer.setBackground(new Color(0xF5F7FA));

        JScrollPane scrollPane = new JScrollPane(tableContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(0xF5F7FA));
        scrollPane.getViewport().setBackground(new Color(0xF5F7FA));
        this.add(scrollPane, BorderLayout.CENTER);

        // Initialize table loading
        controller.loadTables();
    }

    public void renderTables(List<TableStatusDTO> tables) {
        tableContainer.removeAll();
        for (TableStatusDTO dto : tables) {
            tableContainer.add(createTableButton(dto));
        }
        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private String getVietnameseStatus(TableStatusDTO.StatusTable status) {
        return switch (status) {
            case TRONG -> "Trống";
            case CHO_XAC_NHAN -> "Chờ xác nhận";
            case DA_DAT -> "Đã đặt";
            case DANG_SU_DUNG -> "Đang sử dụng";
            default -> "Không rõ";
        };
    }

    public void showDetailDialog(TableStatusDTO dto) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(new Color(0xFFFFFF));

        JLabel[] labels = {
                new JLabel("Tên bàn:"), new JLabel(dto.getTableName()),
                new JLabel("Loại:"), new JLabel(dto.getTableType().name().equals("BAN") ? "Bàn" : "Phòng VIP"),
                new JLabel("Trạng thái:"), new JLabel(getVietnameseStatus(dto.getStatus())),
                new JLabel("Khách hàng:"), new JLabel(dto.getCustomerName() != null ? dto.getCustomerName() : "-"),
                new JLabel("Giờ đặt:"), new JLabel(dto.getReservationTime() != null ? dto.getReservationTime().format(TIME_FORMATTER) : "-"),
                new JLabel("Đơn hàng:"), new JLabel(dto.getOrderStatus() != null ? dto.getOrderStatus().name() : "-")
        };

        for (JLabel label : labels) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(new Color(0x333333));
            panel.add(label);
        }

        JComboBox<TableStatusDTO.StatusTable> statusBox = new JComboBox<>(TableStatusDTO.StatusTable.values());
        statusBox.setSelectedItem(dto.getStatus());
        statusBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusBox.setBackground(Color.WHITE);
        statusBox.setForeground(new Color(0x333333));

        JLabel statusLabel = new JLabel("Chuyển trạng thái:");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(0x333333));
        panel.add(statusLabel);
        panel.add(statusBox);

        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );
        JDialog dialog = optionPane.createDialog(this, "Thông tin chi tiết bàn");
        dialog.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dialog.getContentPane().setBackground(new Color(0xF5F7FA));
        dialog.setVisible(true);

        if (optionPane.getValue() != null && (int) optionPane.getValue() == JOptionPane.OK_OPTION) {
            TableStatusDTO.StatusTable selected = (TableStatusDTO.StatusTable) statusBox.getSelectedItem();
            if (selected != null && selected != dto.getStatus()) {
                controller.updateTableStatus(dto.getTableId(), selected);
            }
        }
    }

    private JButton createTableButton(TableStatusDTO dto) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getColorByStatus(dto.getStatus()));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };

        panel.setPreferredSize(new Dimension(160, 120));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(0, 0, 0, 50)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel tableName = new JLabel(dto.getTableName());
        tableName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableName.setForeground(Color.WHITE);
        tableName.setHorizontalAlignment(SwingConstants.CENTER);
        tableName.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panel.add(tableName, BorderLayout.NORTH);

        // Định dạng thời gian
        String formattedTime = dto.getReservationTime() != null
                ? dto.getReservationTime().format(TIME_FORMATTER)
                : "-";

        JLabel details = new JLabel(
                String.format("<html><center>Khách: %s<br>Giờ đặt: %s<br>Trạng thái: %s</center></html>",
                        dto.getCustomerName() != null ? dto.getCustomerName() : "-",
                        formattedTime,
                        getVietnameseStatus(dto.getStatus())
                )
        );
        details.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        details.setForeground(Color.WHITE);
        details.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(details, BorderLayout.CENTER);

        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.add(panel, BorderLayout.CENTER);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(160, 120));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(dto.getTooltipText());

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(getColorByStatus(dto.getStatus()).brighter());
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(getColorByStatus(dto.getStatus()));
                panel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                controller.showTableDetailDialog(dto);
            }
        });

        return button;
    }
    private Color getColorByStatus(TableStatusDTO.StatusTable status) {
        return switch (status) {
            case TRONG -> new Color(0x2ECC71);
            case CHO_XAC_NHAN -> new Color(0xF1C40F);
            case DA_DAT -> new Color(0x3498DB);
            case DANG_SU_DUNG -> new Color(0xE74C3C);
            default -> new Color(0x95A5A6);
        };
    }
}