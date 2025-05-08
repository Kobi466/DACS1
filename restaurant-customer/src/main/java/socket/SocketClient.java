package socket;

import network.JsonRequest;
import network.JsonResponse;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient {
    private static Socket socket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static boolean isConnected;

    public SocketClient() {
        // Sử dụng ExecutorService để quản lý Thread
        this.isConnected = false;
    }

    /**
     * Kết nối đến server
     * @param host Tên host của server
     * @param port Cổng của server
     */
    public static void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            System.out.println("[SocketClient] Kết nối thành công đến server tại " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("[SocketClient] Lỗi khi kết nối đến server: " + e.getMessage());
        }
    }

    /**
     * Đóng kết nối
     */
    public static void close() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            isConnected = false;
            System.out.println("[SocketClient] Đã đóng kết nối tới server!");
        } catch (IOException e) {
            System.err.println("[SocketClient] Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    /**
     * Gửi request tới server
     * @param request Đối tượng JsonRequest cần gửi
     */
    public static void sendRequest(JsonRequest request) {
        try {
            if (oos != null) {
                oos.writeObject(request);
                oos.flush();
                System.out.println("[SocketClient] Đã gửi request: " + request.getCommand());
            } else {
                System.err.println("[SocketClient] ObjectOutputStream chưa được khởi tạo, không thể gửi request!");
            }
        } catch (IOException e) {
            System.err.println("[SocketClient] Lỗi khi gửi request: " + e.getMessage());
        }
    }

    /**
     * Nhận phản hồi từ server
     * @return Phản hồi dạng JsonResponse, null nếu có lỗi
     */
    public static JsonResponse readResponse() {
        try {
            if (ois != null && isConnected) {
                Object obj = ois.readObject();
                if (obj instanceof JsonResponse response) {
                    System.out.println("[SocketClient] Đã nhận phản hồi: " + response.getStatus());
                    return response;
                } else {
                    System.err.println("[SocketClient] Phản hồi không đúng định dạng JsonResponse!");
                }
            } else {
                System.err.println("[SocketClient] Không thể đọc phản hồi, ObjectInputStream chưa được khởi tạo.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SocketClient] Lỗi khi đọc phản hồi: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lắng nghe phản hồi từ server liên tục
     * @param handler Thực hiện xử lý phản hồi thông qua handler
     */
    public static void listenToServer(ResponseHandler handler) {
        if (!isConnected || socket == null) {
            System.err.println("[SocketClient] Kết nối đến server chưa được thiết lập, không thể lắng nghe.");
            return;
        }

        executorService.submit(() -> {
            try {
                while (!socket.isClosed() && isConnected) {
                    JsonResponse response = readResponse();
                    if (response != null) {
                        handler.handleResponse(response);
                    }
                }
            } catch (Exception e) {
                System.err.println("[SocketClient] Lỗi trong khi lắng nghe server: " + e.getMessage());
            }
        });
    }

    /**
     * Kiểm tra xem client có đang được kết nối tới server hay không
     * @return true nếu kết nối thành công, ngược lại false
     */
    public static boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }
}