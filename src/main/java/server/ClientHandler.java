package server;

import dto.MessageDTO;
import jakarta.persistence.EntityManager;
import model.Message;
import util.HibernateUtil;
import util.XMLUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Map<String, Socket> clients;

    public ClientHandler(Socket socket, Map<String, Socket> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String name = in.readLine(); // client gửi username trước
            clients.put(name, socket);
            System.out.println("👤 " + name + " đã kết nối");

            String xml;
            while ((xml = in.readLine()) != null) {
                MessageDTO msg = XMLUtil.fromXml(xml, MessageDTO.class);

                // 🔃 Forward
                Socket receiverSocket = clients.get(msg.getReceiver());
                if (receiverSocket != null) {
                    PrintWriter receiverOut = new PrintWriter(receiverSocket.getOutputStream(), true);
                    receiverOut.println(xml);
                }

                // 🗃️ Lưu vào DB
                EntityManager em = HibernateUtil.getSessionFactory().createEntityManager();
                em.getTransaction().begin();
                Message m = new Message();
                m.setContent(msg.getContent());
                m.setSender(msg.getSender());
                m.setSent_at(msg.getTimestamp());
                em.persist(m);
                em.getTransaction().commit();
                em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

