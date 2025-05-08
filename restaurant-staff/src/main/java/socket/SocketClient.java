package socket;

import network.JsonRequest;
import network.JsonResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient {
    private static Socket socket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    // Khởi tạo executorService ngay từ đầu
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static boolean isConnected;

    public SocketClient() {
        isConnected = false;
    }

    /**
     * Kết nối đến server
     * @param host Host (hostname hoặc IP của server)
     * @param port Port của server
     */
    public static void connect(String host, int port) {
        if (isConnected) {
            System.err.println("[SocketClient] Đã kết nối trước đó, không cần kết nối lại!");
            return;
        }

        try {
            socket = new Socket(host, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            System.out.println("[SocketClient] Kết nối thành công đến server tại " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("[SocketClient] Lỗi khi kết nối đến server: " + e.getMessage());
            isConnected = false; // Đảm bảo trạng thái kết nối được đặt chính xác
        }
    }

    public static void ensureConnected(String host, int port) {
        if (!isConnected()) {
            System.out.println("[SocketClient] Không có kết nối, đang cố gắng kết nối lại...");
            connect(host, port);
        }
    }

    /**
     * Gửi request tới server
     * @param request JsonRequest để gửi
     */
    public static void sendRequest(JsonRequest request, String host, int port) {
        ensureConnected(host, port); // Đảm bảo kết nối trước khi gửi request

        if (!isConnected) {
            System.err.println("[SocketClient] Vẫn không thể gửi request do không có kết nối!");
            return;
        }

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
     * Lắng nghe phản hồi từ server (đảm bảo trạng thái kết nối trước khi bắt đầu lắng nghe)
     * @param handler Xử lý phản hồi từ server thông qua handler
     */
    public static void listenToServer(String host, int port, ResponseHandler handler) {
        if (!isConnected()) {
            System.err.println("[SocketClient] Kết nối đến server chưa được thiết lập, không thể lắng nghe.");
            connect(host, port); // Thử kết nối lại
        }

        if (!isConnected()) {
            System.err.println("[SocketClient] Không thể bắt đầu lắng nghe do chưa kết nối đến server!");
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
     * Kiểm tra trạng thái kết nối của client
     * @return true nếu đang kết nối thành công
     */
    public static boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

    /**
     * Đóng kết nối với server
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
     * Đọc phản hồi từ server
     * @return JsonResponse, hoặc null nếu có lỗi
     */
    public static JsonResponse readResponse() {
        try {
            if (!isConnected || ois == null) {
                System.err.println("[SocketClient] Không thể đọc phản hồi do kết nối chưa thiết lập!");
                return null;
            }

            Object obj = ois.readObject();
            if (obj instanceof JsonResponse response) {
                System.out.println("[SocketClient] Đã nhận phản hồi: " + response.getStatus());
                return response;
            } else {
                System.err.println("[SocketClient] Format phản hồi không hợp lệ!");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SocketClient] Lỗi khi đọc phản hồi: " + e.getMessage());
        }
        return null;
    }
}