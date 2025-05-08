package view;

import dto.CustomerDTO;
import dto.MessageDTO;
import network.CommandType;
import network.JsonRequest;
import session.ChatHistoryRequest;
import socket.SocketClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class StaffChatPanel extends JFrame {
    private JList<String> customerList; // Danh sách khách hàng
    private DefaultListModel<String> customerListModel;
    private JTextArea chatArea; // Khu vực hiển thị lịch sử chat
    private JTextField inputField; // Khu vực nhập tin nhắn
    private JButton sendButton; // Nút gửi tin nhắn
    private String selectedCustomer; // Khách hàng được chọn

    private final String serverHost = "localhost"; // Thông tin host của server
    private final int serverPort = 8080; // Thông tin port của server

    public StaffChatPanel() {
        checkAndConnectSocket(); // Kiểm tra và kết nối socket tại thời điểm khởi tạo
        initUI();
        loadCustomerList();
    }

    /**
     * Kiểm tra và kết nối socket đến server.
     */
    private void checkAndConnectSocket() {
        SocketClient.ensureConnected(serverHost, serverPort); // Đảm bảo kết nối đến server khi khởi tạo giao diện
        if (!SocketClient.isConnected()) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến server, vui lòng kiểm tra lại!", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Thoát chương trình nếu không kết nối được
        }
    }

    /**
     * Khởi tạo giao diện.
     */
    private void initUI() {
        setTitle("Panel Chat Nhân Viên");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Danh sách khách hàng
        customerListModel = new DefaultListModel<>();
        customerList = new JList<>(customerListModel);
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerList.addListSelectionListener(e -> loadChatHistory());
        JScrollPane customerScrollPane = new JScrollPane(customerList);
        customerScrollPane.setPreferredSize(new Dimension(200, 0));
        customerScrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách khách hàng"));

        // Khu vực hiển thị chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createTitledBorder("Lịch sử tin nhắn"));

        // Khu vực nhập và gửi tin nhắn
        inputField = new JTextField();
        sendButton = new JButton("Gửi");
        sendButton.addActionListener(this::sendMessage); // Gắn hành động gửi tin nhắn cho nút

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Thêm các thành phần vào mainPanel
        mainPanel.add(customerScrollPane, BorderLayout.WEST);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Tải danh sách khách hàng từ server.
     */
    private void loadCustomerList() {
        JsonRequest request = new JsonRequest(CommandType.GET_CUSTOMER_LIST.name(), null);
        SocketClient.sendRequest(request, serverHost, serverPort); // Gửi yêu cầu lên server

        SocketClient.listenToServer(serverHost, serverPort, response -> {
            if ("GET_CUSTOMER_LIST".equals(response.getStatus())) {
                List<CustomerDTO> customers = (List<CustomerDTO>) response.getData();
                customerListModel.clear();
                if (customers != null) {
                    List<String> usernames = customers.stream()
                            .map(CustomerDTO::getUserName)
                            .collect(Collectors.toList());
                    for (String username : usernames) {
                        customerListModel.addElement(username);
                    }
                    JOptionPane.showMessageDialog(this, "Danh sách khách hàng đã tải thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không thể tải danh sách khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    private void showCustomerDetails(String customerUsername) {
        JsonRequest request = new JsonRequest(CommandType.GET_CUSTOMER_DETAILS.name(), customerUsername);
        SocketClient.sendRequest(request, serverHost, serverPort);

        SocketClient.listenToServer(serverHost, serverPort, response -> {
            if ("GET_CUSTOMER_DETAILS".equals(response.getStatus())) {
                CustomerDTO customer = (CustomerDTO) response.getData();
                JOptionPane.showMessageDialog(this,
                        "Tên: " + customer.getUserName() + "\n" +
                                "Số ĐT: " + customer.getSdt(),
                        "Thông tin khách hàng",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(this, "Không thể tải chi tiết khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Tải lịch sử chat với khách hàng.
     */
    private void loadChatHistory() {
        selectedCustomer = customerList.getSelectedValue();
        if (selectedCustomer == null) return;

        chatArea.setText("Đang tải lịch sử trò chuyện với khách hàng...\n");

        ChatHistoryRequest historyRequest = new ChatHistoryRequest(selectedCustomer, "staff");
        JsonRequest request = new JsonRequest(CommandType.GET_CHAT_HISTORY.name(), historyRequest);
        SocketClient.sendRequest(request, serverHost, serverPort);

        SocketClient.listenToServer(serverHost, serverPort, response -> {
            if ("CHAT_HISTORY".equals(response.getStatus())) {
                List<MessageDTO> messages = (List<MessageDTO>) response.getData();
                chatArea.setText(""); // Xóa nội dung cũ
                if (messages != null && !messages.isEmpty()) {
                    for (MessageDTO message : messages) {
                        chatArea.append(message.getSender() + ": " + message.getContent() + "\n");
                    }
                } else {
                    chatArea.append("❌ Không có lịch sử tin nhắn.\n");
                }
            } else {
                chatArea.append("❌ Lỗi tải lịch sử trò chuyện.\n");
            }
        });
    }

    /**
     * Gửi tin nhắn tới khách hàng được chọn.
     */
    private void sendMessage(ActionEvent e) {
        String content = inputField.getText().trim();
        if (content.isEmpty() || selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng và nhập nội dung trước khi gửi!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MessageDTO message = new MessageDTO();
        message.setSender("staff");
        message.setReceiver(selectedCustomer);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now().toString());

        JsonRequest request = new JsonRequest(CommandType.SEND_MESSAGE.name(), message);
        SocketClient.sendRequest(request, serverHost, serverPort);

        SocketClient.listenToServer(serverHost, serverPort, response -> {
            if ("MESSAGE_SENT".equals(response.getStatus())) {
                // Hiển thị tin nhắn vừa được gửi ngay trên giao diện
                chatArea.append("Bạn: " + content + "\n");
                inputField.setText(""); // Xóa nội dung sau khi gửi thành công
            } else {
                JOptionPane.showMessageDialog(this, "Không thể gửi tin nhắn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StaffChatPanel().setVisible(true);
        });
    }
}