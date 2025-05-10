package socketserver;

public class Server {
    public static void main(String[] args) {
        ServerSocketHandler server = new ServerSocketHandler(8080);
        server.start();
    }
}
