package view.restaurantView;

import dao.ReservationDAO;
import dao.TableBookingDAO;
import model.Reservation;
import model.TableBooking;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationManagementPanel extends JPanel {
    private final List<TablePanel> tablePanels = new ArrayList<>();
    private final JPanel detailsPanel;
    private final JTable reservationTable;
    private final DefaultTableModel tableModel;
    private Timer refreshTimer;
    private static final int REFRESH_INTERVAL = 30_000;
    private final JLabel statusLabel;
    private TableBooking.TableType currentFilter = null;
    private JTextField searchField;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Reservation selectedReservation;

    public ReservationManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Khởi tạo components
        detailsPanel = createDetailsPanel();
        tableModel = new DefaultTableModel(
                new String[]{"Mã đặt bàn", "Mã bàn", "Loại bàn", "Khách hàng", "Thời gian", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(tableModel);
        statusLabel = new JLabel("Trạng thái: Đang khởi tạo...");

        setupMainLayout();
//        initializeData();
        startAutoRefresh();
    }

    private void setupMainLayout() {
        // Top Panel với controls
        add(createTopPanel(), BorderLayout.NORTH);

        // Main Split Pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createTablesPanel(),
                createRightPanel());
        mainSplitPane.setResizeWeight(0.6);
        add(mainSplitPane, BorderLayout.CENTER);

        // Status Bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Filter ComboBox
        JComboBox<TableBooking.TableType> filterBox = new JComboBox<>(TableBooking.TableType.values());
        filterBox.insertItemAt(null, 0);
        filterBox.setSelectedItem(null);
        filterBox.setPreferredSize(new Dimension(150, 30));
        filterBox.addActionListener(e -> {
            currentFilter = (TableBooking.TableType) filterBox.getSelectedItem();
            applyFilters();
        });

        // Refresh Button
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> refreshData());

        controlPanel.add(new JLabel("Lọc theo loại: "));
        controlPanel.add(filterBox);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(refreshButton);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm theo tên khách hàng...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        searchPanel.add(new JLabel("Tìm kiếm: "));
        searchPanel.add(searchField);

        panel.add(controlPanel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane createTablesPanel() {
        JPanel tablesPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        tablesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeTablePanels(tablesPanel);

        JScrollPane scrollPane = new JScrollPane(tablesPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Sơ đồ bàn"));
        return scrollPane;
    }

    private JSplitPane createRightPanel() {
        // Reservation Table Panel
        JPanel reservationTablePanel = new JPanel(new BorderLayout());
        reservationTablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách đặt bàn"));
        setupReservationTable();
        reservationTablePanel.add(new JScrollPane(reservationTable), BorderLayout.CENTER);

        // Create Split Pane
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                reservationTablePanel,
                detailsPanel);
        rightSplitPane.setResizeWeight(0.5);
        return rightSplitPane;
    }

    private void setupReservationTable() {
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationTable.setRowHeight(25);
        reservationTable.getTableHeader().setReorderingAllowed(false);

        // Selection Listener
        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedReservationDetails();
            }
        });

        // Custom Renderers
        reservationTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    Reservation.ReservationStatus status =
                            (Reservation.ReservationStatus) table.getModel().getValueAt(row, 5);
                    switch (status) {
                        case CHO_XAC_NHAN:
                            setBackground(new Color(255, 255, 200));
                            break;
                        case DA_XAC_NHAN:
                            setBackground(new Color(200, 255, 200));
                            break;
                        case HUY:
                            setBackground(new Color(255, 200, 200));
                            break;
                    }
                }
                return c;
            }
        });
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết đặt bàn"));

        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Thêm các trường thông tin
        addDetailField(infoPanel, gbc, "Mã đặt bàn:", "reservationIdLabel");
        addDetailField(infoPanel, gbc, "Khách hàng:", "customerLabel");
        addDetailField(infoPanel, gbc, "Bàn số:", "tableLabel");
        addDetailField(infoPanel, gbc, "Thời gian:", "timeLabel");
        addDetailField(infoPanel, gbc, "Trạng thái:", "statusLabel");

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(createActionButtonPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private void addDetailField(JPanel panel, GridBagConstraints gbc, String labelText, String name) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel valueLabel = new JLabel();
        valueLabel.setName(name);
        panel.add(valueLabel, gbc);
        gbc.weightx = 0.0;
    }

    private JPanel createActionButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton confirmButton = new JButton("Xác nhận");
        JButton cancelButton = new JButton("Hủy đặt bàn");
        JButton printButton = new JButton("In thông tin");

        confirmButton.addActionListener(e -> handleConfirmReservation());
        cancelButton.addActionListener(e -> handleCancelReservation());
        printButton.addActionListener(e -> handlePrintReservation());

        panel.add(confirmButton);
        panel.add(cancelButton);
        panel.add(printButton);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    private void initializeTablePanels(JPanel container) {
        try {
            List<TableBooking> tables = TableBookingDAO.getInstance().selectAll();
            for (TableBooking table : tables) {
                TablePanel tablePanel = new TablePanel(table);
                tablePanels.add(tablePanel);
                container.add(tablePanel);
            }
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách bàn", e);
        }
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = ReservationDAO.getInstance().getCurrentReservations();
            updateTableModel(reservations);
            updateTablePanels(reservations);
            statusLabel.setText("Trạng thái: Đã cập nhật dữ liệu lúc " +
                    LocalDateTime.now().format(dtf));
        } catch (Exception e) {
            showError("Lỗi khi tải thông tin đặt bàn", e);
        }
    }

    private void updateTableModel(List<Reservation> reservations) {
        tableModel.setRowCount(0);
        for (Reservation reservation : reservations) {
            tableModel.addRow(new Object[]{
                    reservation.getReservationId(),
                    reservation.getTableBooking().getTables_id(),
                    reservation.getTableBooking().getTableType(),
                    reservation.getCustomer().getUserName(),
                    reservation.getBookingTime().format(dtf),
                    reservation.getStatus()
            });
        }
    }

    private void updateTablePanels(List<Reservation> reservations) {
        // Reset all tables
        for (TablePanel panel : tablePanels) {
            panel.setReservation(null);
        }

        // Update tables with reservations
        for (Reservation reservation : reservations) {
            TablePanel panel = findTablePanel(reservation.getTableBooking().getTables_id());
            if (panel != null) {
                panel.setReservation(reservation);
            }
        }
    }

    private TablePanel findTablePanel(int tableId) {
        return tablePanels.stream()
                .filter(panel -> panel.getTable().getTables_id() == tableId)
                .findFirst()
                .orElse(null);
    }

    private void showSelectedReservationDetails() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow >= 0) {
            int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                selectedReservation = ReservationDAO.getInstance().selecById(reservationId);
                updateDetailsPanel(selectedReservation);
            } catch (Exception e) {
                showError("Lỗi khi tải chi tiết đặt bàn", e);
            }
        }
    }

    private void updateDetailsPanel(Reservation reservation) {
        Component[] components = ((JPanel)detailsPanel.getComponent(0)).getComponents();
        for (Component component : components) {
            if (component instanceof JLabel && component.getName() != null) {
                JLabel label = (JLabel) component;
                switch (label.getName()) {
                    case "reservationIdLabel":
                        label.setText(String.valueOf(reservation.getReservationId()));
                        break;
                    case "customerLabel":
                        label.setText(reservation.getCustomer().getUserName());
                        break;
                    case "tableLabel":
                        label.setText(reservation.getTableBooking().toString());
                        break;
                    case "timeLabel":
                        label.setText(reservation.getBookingTime().format(dtf));
                        break;
                    case "statusLabel":
                        label.setText(reservation.getStatus().toString());
                        break;
                }
            }
        }
    }

    private void handleConfirmReservation() {
        if (selectedReservation != null &&
                selectedReservation.getStatus() == Reservation.ReservationStatus.CHO_XAC_NHAN) {
            try {
                ReservationDAO.getInstance().updateReservationStatus(
                        selectedReservation.getReservationId(),
                        Reservation.ReservationStatus.DA_XAC_NHAN);
                refreshData();
            } catch (Exception e) {
                showError("Lỗi khi xác nhận đặt bàn", e);
            }
        }
    }

    private void handleCancelReservation() {
        if (selectedReservation != null &&
                selectedReservation.getStatus() != Reservation.ReservationStatus.HUY) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn hủy đặt bàn này?",
                    "Xác nhận hủy",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ReservationDAO.getInstance().updateReservationStatus(
                            selectedReservation.getReservationId(),
                            Reservation.ReservationStatus.HUY);
                    refreshData();
                } catch (Exception e) {
                    showError("Lỗi khi hủy đặt bàn", e);
                }
            }
        }
    }

    private void handlePrintReservation() {
        if (selectedReservation != null) {
            // TODO: Implement printing functionality
            JOptionPane.showMessageDialog(this,
                    "Chức năng in đang được phát triển",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        for (TablePanel panel : tablePanels) {
            boolean matchesFilter = currentFilter == null ||
                    panel.getTable().getTableType() == currentFilter;
            boolean matchesSearch = searchText.isEmpty() ||
                    (panel.getReservation() != null &&
                            panel.getReservation().getCustomer().getUserName()
                                    .toLowerCase().contains(searchText));
            panel.setVisible(matchesFilter && matchesSearch);
        }
    }

    private void refreshData() {
        statusLabel.setText("Trạng thái: Đang cập nhật...");
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                loadReservations();
                return null;
            }
        };
        worker.execute();
    }

    private void startAutoRefresh() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
        refreshTimer = new Timer(REFRESH_INTERVAL, e -> refreshData());
        refreshTimer.start();
    }

    private void showError(String message, Exception e) {
        statusLabel.setText("Trạng thái: Lỗi - " + message);
        JOptionPane.showMessageDialog(this,
                message + ": " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }

    // Inner class for table panels
    private class TablePanel extends JPanel {
        private final TableBooking table;
        private Reservation reservation;
        private final Color AVAILABLE_COLOR = new Color(144, 238, 144);
        private final Color RESERVED_COLOR = new Color(255, 99, 71);
        private final Color VIP_COLOR = new Color(255, 215, 0);

        public TablePanel(TableBooking table) {
            this.table = table;
            setPreferredSize(new Dimension(150, 150));
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            setBackground(AVAILABLE_COLOR);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (reservation != null) {
                        int row = findReservationRow(reservation.getReservationId());
                        if (row >= 0) {
                            reservationTable.setRowSelectionInterval(row, row);
                            reservationTable.scrollRectToVisible(
                                    reservationTable.getCellRect(row, 0, true));
                        }
                    }
                }
            });
        }

        private int findReservationRow(int reservationId) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int)tableModel.getValueAt(i, 0) == reservationId) {
                    return i;
                }
            }
            return -1;
        }

        public TableBooking getTable() {
            return table;
        }

        public Reservation getReservation() {
            return reservation;
        }

        public void setReservation(Reservation reservation) {
            this.reservation = reservation;
            updateAppearance();
        }

        private void updateAppearance() {
            if (table.getTableType() == TableBooking.TableType.PHONG_VIP) {
                setBackground(VIP_COLOR);
            } else {
                setBackground(reservation != null ? RESERVED_COLOR : AVAILABLE_COLOR);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw table symbol
            int padding = 20;
            int width = getWidth() - 2 * padding;
            int height = getHeight() - 2 * padding;

            g2d.setColor(Color.BLACK);
            g2d.drawRect(padding, padding, width, height);

            // Draw text
            String text = table.getTableType() == TableBooking.TableType.PHONG_VIP ?
                    "Phòng VIP " : "Bàn ";
            text += table.getTables_id();
            if (reservation != null) {
                text += "\n" + reservation.getCustomer().getUserName();
            }

            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            int textY = getHeight() / 2;
            g2d.drawString(text, textX, textY);
        }
    }
}