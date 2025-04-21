package vn.kobi;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Client Chat");
    private JTextArea messageArea = new JTextArea();
    private JTextField inputField = new JTextField();

    public ChatClient(String serverAddress) throws IOException {
        Socket socket = new Socket(serverAddress, 10000);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        messageArea.setEditable(false);
        inputField.addActionListener(e -> {
            out.println(inputField.getText());
            messageArea.append("Bạn: " + inputField.getText() + "\n");
            inputField.setText("");
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.add(inputField, BorderLayout.SOUTH);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Thread nhận tin nhắn từ server
        new Thread(() -> {
            String msg;
            try {
                while ((msg = in.readLine()) != null) {
                    messageArea.append("Khác: " + msg + "\n");
                }
            } catch (IOException e) {
                messageArea.append("Ngắt kết nối tới server.\n");
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        String serverAddress = JOptionPane.showInputDialog("Nhập địa chỉ server:");
        new ChatClient(serverAddress);
    }
}

