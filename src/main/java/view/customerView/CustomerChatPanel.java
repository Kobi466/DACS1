package view;

import dto.MessageDTO;
import model.Customer;
import model.Message;
import util.XMLUtil;
import dao.MessageDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class CustomerChatPanel extends JPanel {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private final Customer customer;

    public CustomerChatPanel(Customer customer) {
        this.customer = customer;
        setLayout(new BorderLayout());
        initComponents();
        connectToServer();
    }

    private void initComponents() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        inputField = new JTextField();
        sendButton = new JButton("Gửi");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(this::sendMessage);
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 10000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi tên người dùng đầu tiên để server nhận diện
            out.println(customer.getUserName());

            // Thread nhận tin nhắn
            new Thread(this::listenForMessages).start();

        } catch (IOException e) {
            showError("❌ Không thể kết nối đến server: " + e.getMessage());
        }
    }

    private void sendMessage(ActionEvent e) {
        String content = inputField.getText().trim();
        if (content.isEmpty()) return;

        try {
            MessageDTO dto = new MessageDTO();
            dto.setSender(customer.getUserName());
            dto.setReceiver("staff");
            dto.setContent(content);
            dto.setTimestamp(LocalDateTime.now());

            String xml = XMLUtil.toXml(dto);
            out.println(xml);
            inputField.setText("");
            chatArea.append("Bạn: " + content + "\n");

            // Ghi vào DB
            Message msg = new Message();
            msg.setSender("Customer");
            msg.setReceiver("Staff");
            msg.setContent(content);
            msg.setSent_at(dto.getTimestamp());
            msg.setCustomer(customer);

            MessageDAO.getInstance().insert(msg);

        } catch (Exception ex) {
            showError("Gửi tin nhắn thất bại: " + ex.getMessage());
        }
    }

    private void listenForMessages() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                MessageDTO dto = XMLUtil.fromXml(line, MessageDTO.class);
                chatArea.append(dto.getSender() + ": " + dto.getContent() + "\n");
            }
        } catch (Exception e) {
            showError("💬 Kết nối bị gián đoạn: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
