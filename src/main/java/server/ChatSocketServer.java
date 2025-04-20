package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSocketServer {
    private static Map<String, Socket> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(10000);
        System.out.println("💬 Chat Server Started...");

        while (true) {
            Socket socket = server.accept();
            new Thread(new ClientHandler(socket, clients)).start();
        }
    }
}

