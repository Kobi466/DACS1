package socket;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private static boolean isConnected = false;

    // ExecutorService để xử lý các yêu cầu từ server
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Kết nối đến server
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
            isConnected = false;
        }
    }

    // Đảm bảo kết nối nếu chưa có
    public static void ensureConnected(String host, int port) {
        if (!isConnected()) {
            connect(host, port);
        }
    }

    // Kiểm tra trạng thái kết nối
    public static boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

    // Gửi yêu cầu đến server
    public static void sendRequest(JsonRequest request, String host, int port) {
        ensureConnected(host, port);
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

    // Lắng nghe phản hồi từ server trong một luồng riêng
    // Ở listenToServer, đừng tạo thread mới nếu bạn đã có sẵn executor
    public static void listenToServer(String host, int port, ResponseHandler handler) {
        ensureConnected(host, port);
        if (!isConnected) {
            System.err.println("[SocketClient] Không thể bắt đầu lắng nghe do chưa kết nối đến server!");
            return;
        }

        executorService.submit(() -> {
            try {
                while (!socket.isClosed() && isConnected) {
                    JsonResponse response = readResponse();
                    if (response != null) {
                        handler.handleResponse(response); // gọi lại xử lý UI
                    }
                }
            } catch (Exception e) {
                System.err.println("[SocketClient] Lỗi khi lắng nghe từ server: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }


    // Đọc phản hồi từ server
    private static JsonResponse readResponse() {
        try {
            if (ois != null) {
                return (JsonResponse) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SocketClient] Lỗi khi nhận dữ liệu từ server: " + e.getMessage());
            isConnected = false; // Gợi ý thêm dòng này
        }
        return null;
    }


    // Đóng kết nối
    public static void closeConnection() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            isConnected = false;
            System.out.println("[SocketClient] Đã đóng kết nối.");
        } catch (IOException e) {
            System.err.println("[SocketClient] Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    // Xử lý phản hồi từ server
    public interface ResponseHandler {
        void handleResponse(JsonResponse response) throws JsonProcessingException;
    }
}
