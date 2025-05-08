package socketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSocketHandler extends Thread {
    private int port;
    private ServerSocket serverSocket;
    // Lưu các client đang online: clientID → handler
    public static ConcurrentHashMap<String, ClientHandler> clientMap = new ConcurrentHashMap<>();

    public ServerSocketHandler(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("✅ Server is running on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start(); // Bắt đầu xử lý client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
