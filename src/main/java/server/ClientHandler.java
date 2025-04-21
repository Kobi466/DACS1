package server;

import dao.MessageDAO;
import dto.MessageDTO;
import jakarta.xml.bind.JAXBException;
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

                this.userType = msg.getSender(); // Gán theo sender để phân loại

                System.out.println("📩 " + msg.getSender() + " -> " + msg.getReceiver() + ": " + msg.getContent());

                new MessageDAO().save(msg);

                // Gửi đến người nhận
                for (ClientHandler client : ChatSocketServer.clients) {
                    if (client != this && client.userType != null && client.userType.equals(msg.getReceiver())) {
                        client.sendMessage(XMLUtil.toXML(msg));
                    }
                }
            }
        } catch (IOException | JAXBException e) {
            System.err.println("❌ Client mất kết nối: " + e.getMessage());
        } finally {
            ChatSocketServer.clients.remove(this);
        }
    }
}
