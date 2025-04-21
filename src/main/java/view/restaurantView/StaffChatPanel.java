package view.restaurantView;

import dto.MessageDTO;
import jakarta.xml.bind.JAXBException;
import util.XMLUtil;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class StaffChatPanel extends JPanel {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public StaffChatPanel() {
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Gửi");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

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
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối server", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupListeners() {
        sendButton.addActionListener(e -> {
            try {
                sendMessage(e);
            } catch (JAXBException ex) {
                throw new RuntimeException(ex);
            }
        });
        inputField.addActionListener(e -> {
            try {
                sendMessage(e);
            } catch (JAXBException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void sendMessage(ActionEvent e) throws JAXBException {
        String content = inputField.getText().trim();
        if (!content.isEmpty()) {
            MessageDTO msg = new MessageDTO("Staff", "Customer", content, LocalDateTime.now());
            String xml = XMLUtil.toXML(msg);
            out.println(xml);
            appendMessage("Bạn: " + content);
            inputField.setText("");
        }
    }

    private void startReceiveThread() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    MessageDTO msg = XMLUtil.fromXML(line, MessageDTO.class);
                    if ("Customer".equals(msg.getSender())) {
                        appendMessage("Khách hàng: " + msg.getContent());
                    }
                }
            } catch (IOException e) {
                appendMessage("❌ Mất kết nối đến server.");
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void appendMessage(String message) {
        chatArea.append(message + "\n");
    }
}

