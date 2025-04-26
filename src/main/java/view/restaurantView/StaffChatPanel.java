package view.restaurantView;

import dto.MessageDTO;
import jakarta.xml.bind.JAXBException;
import util.ChatLoader;
import util.XMLUtil;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StaffChatPanel extends JPanel {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JComboBox<String> customerSelector;

    private final Map<String, Integer> customerMap = new HashMap<>();
    private String currentCustomer;

    public StaffChatPanel() {
        setLayout(new BorderLayout());

        customerSelector = new JComboBox<>();
        customerSelector.addItem("Thông báo từ khách hàng");
        customerSelector.addActionListener(e -> {
            currentCustomer = (String) customerSelector.getSelectedItem();
            chatArea.setText("");

            if (currentCustomer == null || currentCustomer.equals("Thông báo từ khách hàng")) return;

            new ChatLoader(currentCustomer, chatArea).execute();
        });



        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Gửi");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(customerSelector, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setupSocket();
        setupListeners();
        startReceiveThread();
    }

    private void setupSocket() {
        try {
            socket = new Socket("localhost", 10000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 🔥 GỬI INIT TIN NHẮN ĐỂ SERVER NHẬN BIẾT STAFF
            MessageDTO init = new MessageDTO("Staff", "Server", "Staff Connected", LocalDateTime.now(), id);
            String initXml = XMLUtil.toXML(init);
            out.println(initXml);

        } catch (IOException | JAXBException e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối server", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void setupListeners() {
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String content = inputField.getText().trim();
        if (content.isEmpty() || currentCustomer == null || "Thông báo từ khách hàng".equals(currentCustomer)) {
            return;
        }

        Integer customerId = customerMap.get(currentCustomer);
        if (customerId == null) return;

        MessageDTO msg = new MessageDTO("Staff", currentCustomer, content, LocalDateTime.now(), customerId);
        try {
            String xml = XMLUtil.toXML(msg);
            out.println(xml);
            appendMessage("Bạn: " + content);
            inputField.setText("");
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
    }
    Integer id = 0;
    private void startReceiveThread() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    MessageDTO msg = XMLUtil.fromXML(line, MessageDTO.class);
                    if (!"Staff".equals(msg.getSender())) {
                        String sender = msg.getSender();
                        id = msg.getCustomerID();

                        System.out.println("📩 Nhận message từ khách: " + sender + " - ID = " + id);

                        customerMap.putIfAbsent(sender, id);

                        SwingUtilities.invokeLater(() -> {
                            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) customerSelector.getModel();
                            if (model.getIndexOf(sender) == -1) {
                                customerSelector.addItem(sender);
                                customerSelector.repaint();
                                System.out.println("✅ Thêm " + sender + " vào ComboBox");
                                JOptionPane.showMessageDialog(this, "Bạn đã nhận được tin nhắn mới");
                            }
                        });
                        if (sender.equals(currentCustomer)) {
                            appendMessage(sender + ": " + msg.getContent());
                        }
                    }
                }
            } catch (IOException | JAXBException e) {
                appendMessage("❌ Mất kết nối đến server.");
            }
        }).start();
    }

    private void appendMessage(String message) {
        chatArea.append(message + "\n");
    }
}
