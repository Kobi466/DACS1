package server;

import dao.MessageDAO;
import dto.MessageDTO;
import util.XMLUtil;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedWriter writer;
    private String userType;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void sendMessage(String xml) {
        try {
            writer.write(xml);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("❌ Gửi thất bại: " + e.getMessage());
        }
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                MessageDTO msg = XMLUtil.fromXML(inputLine, MessageDTO.class);

                this.userType = msg.getSender(); // lưu tên user
                ChatSocketServer.clients.putIfAbsent(msg.getSender(), this);

                System.out.println("📩 " + msg.getSender() + " -> " + msg.getReceiver() + ": " + msg.getContent());

                new MessageDAO().saveMessage(msg);

                ClientHandler receiverHandler = ChatSocketServer.clients.get(msg.getReceiver());
                if (receiverHandler != null) {
                    String xml = XMLUtil.toXML(msg); // Gửi XML, không chỉ content!
                    receiverHandler.sendMessage(xml);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Client mất kết nối: " + e.getMessage());
        } finally {
            if (userType != null) {
                ChatSocketServer.clients.remove(userType);
            }
        }
    }
}
