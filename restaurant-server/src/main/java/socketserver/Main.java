package socketserver;

public class Main {
    public static void main(String[] args) {
        ServerSocketHandler server = new ServerSocketHandler(8080);
        server.start();
    }
}
