package view;

import dto.CustomerDTO;
import dto.MessageDTO;
import network.JsonRequest;
import session.ChatHistoryRequest;
import socket.SocketClient;
import service.ChatService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;

public class StaffChatPanel extends JFrame {
    private JList<String> customerList;
    private DefaultListModel<String> customerListModel;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private String selectedCustomer;

    private final String serverHost = "localhost";
    private final int serverPort = 8080;

    public StaffChatPanel() {
        checkAndConnectSocket();
        ChatService.notifyStaffOnline(); // 👈 Gửi lệnh thông báo staff đã kết nối
        initUI();
        startListening();
        loadCustomerList();
        loadChatHistory();
    }

    // BỎ thread phụ — SocketClient đã xử lý thread rồi
    private void startListening() {
        SocketClient.listenToServer(serverHost, serverPort, response -> {
            SwingUtilities.invokeLater(() -> {
                switch (response.getStatus()) {
                    case "MESSAGE_RECEIVED", "NEW_MESSAGE" -> {
                        System.out.println("📨 Nhận tin nhắn mới: " + response.getData());
                        handleIncomingMessage((MessageDTO) response.getData());
                    }
                    case "MESSAGE_SENT" -> System.out.println("✅ Tin nhắn đã gửi thành công.");
                    case "CHAT_HISTORY" -> {
                        System.out.println("⏬ Nhận lịch sử chat từ server: " + response.getData());
                        handleChatHistory((List<MessageDTO>) response.getData());
                    }
                    case "GET_CUSTOMER_LIST" -> handleCustomerList((List<CustomerDTO>) response.getData());
                    default -> System.out.println("Không xử lý được response: " + response.getStatus());
                }

            });
        });
    }



    private void handleIncomingMessage(MessageDTO message) {
        String sender = message.getSender();
        String cleanSelected = getCleanSelectedCustomer();

        // Nếu khách chưa có trong danh sách → thêm
        if (!customerListModel.contains(sender)) {
            customerListModel.addElement(sender);
        }

        // ✅ Nếu đang chat đúng khách → hiển thị luôn
        if (cleanSelected != null && cleanSelected.equals(sender)) {
            ChatService.appendMessageToChat(chatArea, sender, message.getContent(), message.getSentAt());
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        } else {
            // ✅ Chỉ đánh dấu người khác có tin nhắn mới — không load
            System.out.println("🔔 Tin nhắn từ khách khác: " + sender);
            // 👉 Có thể highlight UI, hoặc làm gì đó nếu cần
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
        chatArea.setText(""); // Xóa trước

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
            System.exit(1);
        }
    }

    private void initUI() {
        setTitle("Nhân viên - Giao tiếp khách hàng");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        customerListModel = new DefaultListModel<>();
        customerList = new JList<>(customerListModel);
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String newSelected = customerList.getSelectedValue();
                if (newSelected == null) return;

                selectedCustomer = newSelected;

                // Xóa dấu (*)
                int index = customerListModel.indexOf(newSelected);
                if (index != -1 && customerListModel.get(index).contains("(*)")) {
                    customerListModel.set(index, newSelected);
                }

                // Load lại chat
                ChatService.loadChatHistory(selectedCustomer);
            }
        });



        JScrollPane customerScrollPane = new JScrollPane(customerList);
        customerScrollPane.setPreferredSize(new Dimension(200, 0));
        customerScrollPane.setBorder(BorderFactory.createTitledBorder("📋 Khách hàng"));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createTitledBorder("💬 Lịch sử tin nhắn"));

        inputField = new JTextField();
        sendButton = new JButton("Gửi");
        sendButton.addActionListener(this::sendMessage);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(customerScrollPane, BorderLayout.WEST);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffChatPanel().setVisible(true));
    }
}
