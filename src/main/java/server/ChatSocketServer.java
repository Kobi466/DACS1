package server;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSocketServer {
    public static Map<String, ClientHandler> clients = new ConcurrentHashMap<>();//danh sach client


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(10000)) {
            System.out.println("🚀 Chat server started...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.get(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
