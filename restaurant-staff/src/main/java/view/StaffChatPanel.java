package view;

import dto.CustomerDTO;
import dto.MessageDTO;
import network.GlobalResponseRouter;
import socket.SocketClient;
import service.ChatService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;

public class StaffChatPanel extends JPanel {
    private JList<String> customerList;
    private DefaultListModel<String> customerListModel;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private String selectedCustomer;

    // Màu sắc theo phong cách nhà hàng
    private final Color BACKGROUND_COLOR = new Color(255, 250, 240); // Màu nền kem nhẹ
    private final Color PRIMARY_COLOR = new Color(139, 69, 19);     // Màu nâu đậm
    private final Color ACCENT_COLOR = new Color(210, 105, 30);     // Màu cam đất
    private final Color TEXT_COLOR = new Color(60, 30, 10);         // Màu nâu đậm cho text
    private final Font TITLE_FONT = new Font("Serif", Font.BOLD, 14);
    private final Font CONTENT_FONT = new Font("SansSerif", Font.PLAIN, 13);

    private final String serverHost = "localhost";
    private final int serverPort = 8080;

    public StaffChatPanel() {
        checkAndConnectSocket();
        ChatService.notifyStaffOnline(); // 👈 Gửi lệnh thông báo staff đã kết nối
        initUI();
        startListening();
        loadCustomerList();
    }

    private void startListening() {
        GlobalResponseRouter.addListener(response -> {
            SwingUtilities.invokeLater(() -> {
                switch (response.getStatus()) {
                    case "MESSAGE_RECEIVED", "NEW_MESSAGE" -> {
                        System.out.println("📨 Nhận tin nhắn mới: " + response.getData());
                        handleIncomingMessage((MessageDTO) response.getData());
                    }
                    case "SEND_SUCCESS" -> System.out.println("✅ Tin nhắn đã gửi thành công.");
                    case "CHAT_HISTORY_SUCCESS" -> {
                        System.out.println("⏬ Nhận lịch sử chat từ server: " + response.getData());
                        handleChatHistory((List<MessageDTO>) response.getData());
                    }
                    case "GET_CUSTOMER_LIST" -> handleCustomerList((List<CustomerDTO>) response.getData());
                    case "STAFF_JOINED" -> System.out.println("👨‍💼 Staff đã kết nối thành công!");
                    default -> System.out.println("Không xử lý được response: " + response.getStatus());
                }
            });
        });
    }

    private void handleIncomingMessage(MessageDTO message) {
        String sender = message.getSender();
        String cleanSelected = getCleanSelectedCustomer();

        if (!customerListModel.contains(sender)) {
            customerListModel.addElement(sender);
        }

        if (cleanSelected != null && cleanSelected.equals(sender)) {
            ChatService.appendMessageToChat(chatArea, sender, message.getContent(), message.getSentAt());
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        } else {
            System.out.println("🔔 Tin nhắn từ khách khác: " + sender);
        }
    }

    private String getCleanSelectedCustomer() {
        if (selectedCustomer == null) return null;
        return selectedCustomer.replace(" (*)", "").trim();
    }

    private void handleCustomerList(List<CustomerDTO> customers) {
        customerListModel.clear();
        for (CustomerDTO c : customers) {
            customerListModel.addElement(c.getUserName());
        }
    }

    private void handleChatHistory(List<MessageDTO> messages) {
        chatArea.setText("");

        if (messages == null || messages.isEmpty()) {
            chatArea.append("❌ Không có lịch sử tin nhắn.\n");
            return;
        }

        for (MessageDTO m : messages) {
            ChatService.appendMessageToChat(chatArea, m.getSender(), m.getContent(), m.getSentAt());
        }

        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void sendMessage(ActionEvent e) {
        String messageText = inputField.getText().trim();
        if (messageText.isEmpty() || selectedCustomer == null) return;

        ChatService.sendMessage("staff", selectedCustomer, messageText);
        ChatService.appendMessageToChat(chatArea, "Bạn", messageText, LocalDateTime.now().toString());
        inputField.setText("");
    }

    private void loadCustomerList() {
        ChatService.loadCustomerList();
    }

    private void loadChatHistory() {
        selectedCustomer = customerList.getSelectedValue();
        if (selectedCustomer == null) return;

        ChatService.loadChatHistory(getCleanSelectedCustomer());
    }

    private void checkAndConnectSocket() {
        SocketClient.ensureConnected(serverHost, serverPort);
        if (!SocketClient.isConnected()) {
            JOptionPane.showMessageDialog(this, "❌ Không thể kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(BACKGROUND_COLOR);

        // Panel tiêu đề
        JPanel headerPanel = createHeaderPanel();

        // Danh sách khách hàng
        customerListModel = new DefaultListModel<>();
        customerList = new JList<>(customerListModel);
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerList.setFont(new Font("SansSerif", Font.BOLD, 14));
        customerList.setBackground(new Color(253, 245, 230)); // Màu nền danh sách
        customerList.setForeground(TEXT_COLOR);
        customerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String newSelected = customerList.getSelectedValue();
                if (newSelected == null) return;

                selectedCustomer = newSelected;

                int index = customerListModel.indexOf(newSelected);
                if (index != -1 && customerListModel.get(index).contains("(*)")) {
                    customerListModel.set(index, newSelected);
                }

                ChatService.loadChatHistory(selectedCustomer);
            }
        });

        JScrollPane customerScrollPane = new JScrollPane(customerList);
        customerScrollPane.setPreferredSize(new Dimension(250, 0));
        TitledBorder customerBorder = BorderFactory.createTitledBorder("🍽️ Danh sách thực khách");
        customerBorder.setTitleFont(new Font("Serif", Font.BOLD, 16));
        customerBorder.setTitleColor(PRIMARY_COLOR);
        customerScrollPane.setBorder(BorderFactory.createCompoundBorder(
                customerBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Khu vực chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(CONTENT_FONT);
        chatArea.setBackground(new Color(255, 255, 245)); // Màu nền nhẹ cho vùng chat
        chatArea.setForeground(TEXT_COLOR);

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        TitledBorder chatBorder = BorderFactory.createTitledBorder("💬 Cuộc hội thoại");
        chatBorder.setTitleFont(TITLE_FONT);
        chatBorder.setTitleColor(PRIMARY_COLOR);
        chatScrollPane.setBorder(chatBorder);

        // Khu vực nhập liệu
        inputField = new JTextField();
        inputField.setFont(CONTENT_FONT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        sendButton = new JButton("Gửi");
        sendButton.setFont(TITLE_FONT);
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(this::sendMessage);

        JButton suggestBtn = createSuggestionButton();

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(suggestBtn, BorderLayout.WEST);

        // Thêm các thành phần vào panel chính
        add(headerPanel, BorderLayout.NORTH);
        add(customerScrollPane, BorderLayout.WEST);
        add(chatScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Hệ Thống Chat Nhà Hàng");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);

        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JButton createSuggestionButton() {
        JButton suggestBtn = new JButton("🍽️ Gợi ý");
        suggestBtn.setFont(TITLE_FONT);
        suggestBtn.setBackground(ACCENT_COLOR);
        suggestBtn.setForeground(Color.WHITE);
        suggestBtn.setFocusPainted(false);
        suggestBtn.addActionListener(e -> {
            JPopupMenu menu = new JPopupMenu();
            menu.setBackground(BACKGROUND_COLOR);
            menu.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));

            String[] suggestions = {
                    "Kính chào quý khách, nhà hàng chúng tôi có thể giúp gì ạ?",
                    "Quý khách muốn đặt bàn vào lúc mấy giờ ạ?",
                    "Món đặc sản này vẫn còn sẵn phục vụ quý khách!",
                    "Đã xác nhận đơn hàng, xin cảm ơn quý khách.",
                    "Nhà hàng có món đặc biệt hôm nay là cá hồi nướng, quý khách có muốn thử không ạ?",
                    "Nhà hàng sẽ chuẩn bị bàn cho quý khách trong vòng 15 phút."
            };

            for (String s : suggestions) {
                JMenuItem item = new JMenuItem(s);
                item.setFont(CONTENT_FONT);
                item.setForeground(TEXT_COLOR);
                item.addActionListener(ev -> inputField.setText(s));
                menu.add(item);
            }

            menu.show(suggestBtn, 0, suggestBtn.getHeight());
        });

        return suggestBtn;
    }
}