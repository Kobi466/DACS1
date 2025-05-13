package network;

import socket.SocketClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GlobalResponseRouter {

    private static final List<Consumer<JsonResponse>> listeners = new ArrayList<>();

    public static void startListening(String serverHost, int serverPort) {
            SocketClient.listenToServer(serverHost, serverPort, response -> {
            if (response == null) {
                System.err.println("⚠️ Response null, bỏ qua.");
                return;
            }
            synchronized (listeners) {
                for (Consumer<JsonResponse> listener : listeners) {
                    listener.accept(response);
                }
            }
        });
    }

    public static void addListener(Consumer<JsonResponse> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public static void removeListener(Consumer<JsonResponse> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
}
