package socket;

/**
 * Interface để xử lý các phản hồi (JsonResponse) nhận được từ server.
 */
@FunctionalInterface
public interface ResponseHandler {
    /**
     * Hàm được gọi khi nhận phản hồi từ server.
     *
     * @param response JsonResponse từ server.
     */
    void handleResponse(network.JsonResponse response);
}