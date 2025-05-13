package view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.OrderController;
import dto.CustomerDTO;
import dto.MessageDTO;
import dto.OrderSummaryDTO;
import network.GlobalResponseRouter;
import socket.SocketClient;
import util.JSONUtil;
import util.JacksonUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private final OrderController controller;

    public OrderPanel() {
        this.controller = new OrderController(this);
        setupUI();
        registerListeners();
        controller.loadOrders();
    }
//    private void setupAutoRefresh() {
//        Timer timer = new Timer(5000, e -> controller.loadOrders());
//        timer.start();
//    }


    private void setupUI() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"Tên Khách Hàng", "Số điện thoại", "Thời gian", "Trạng thái"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void updateOrderTable(List<OrderSummaryDTO> orders) {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            for (OrderSummaryDTO order : orders) {
                model.addRow(new Object[]{
                        order.getCustomerName(),
                        order.getCustomerPhone(),
                        order.getOrderDate(),
                        order.getStatus().name()
                });
            }
        });
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void registerListeners() {
        GlobalResponseRouter.addListener(response -> {
            switch (response.getStatus()) {
                case "GET_ORDERS_SUCCESS" -> {
                    List<OrderSummaryDTO> orders = (List<OrderSummaryDTO>) response.getData();
                    updateOrderTable(orders);
                    System.out.println("✅ Đã nhận danh sách đơn hàng.");
                }
                case "NEW_ORDER_CREATED" -> {
                    // Đơn hàng mới tạo, thêm 1 dòng vào bảng
                    OrderSummaryDTO newOrder = JacksonUtils.getObjectMapper().convertValue(
                            response.getData(), OrderSummaryDTO.class
                    );
                    addOrderToTable(newOrder);
                    System.out.println("🆕 Đơn hàng mới đã được thêm: " + newOrder.getCustomerName());
                }
                default -> System.out.println("⚠️ Không hiểu phản hồi: " + response.getStatus());
            }
        });
    }
    private void addOrderToTable(OrderSummaryDTO order) {
        SwingUtilities.invokeLater(() -> {
            model.addRow(new Object[]{
                    order.getCustomerName(),
                    order.getCustomerPhone(),
                    order.getOrderDate(),
                    order.getStatus().name()
            });
        });
    }
}
