package vn.kobi;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private List<ClientHandler> clients;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String msg;
        try {
            while ((msg = in.readLine()) != null) {
                broadcast(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clients.remove(this);
            try { socket.close(); } catch (IOException e) {}
        }
    }

    private void broadcast(String msg) {
        for (ClientHandler client : clients) {
            if (client != this) {
                client.out.println(msg);
            }
        }
    }
}


