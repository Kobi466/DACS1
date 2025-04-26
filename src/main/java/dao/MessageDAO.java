package dao;

import dto.MessageDTO;
import model.Customer;
import model.Message;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;
import java.util.List;

public class MessageDAO extends AbstractDAO<Message, Integer>{
    public static MessageDAO getInstance(){
        return new MessageDAO();
    }
    public MessageDAO() {
        super(Message.class);
    }
    public List<Message> findByCustomer(Customer customer) {
        return em.createQuery("FROM Message WHERE customer = :customer ORDER BY timestamp", Message.class)
                .setParameter("customer", customer)
                .getResultList();
    }
    public List<Message> getMessagesBetween(String sender, String receiver) {
        try {
            return em.createQuery(
                            "FROM Message WHERE (sender = :sender AND receiver = :receiver) " +
                                    "OR (sender = :receiver AND receiver = :sender) ORDER BY sent_at ASC", Message.class)
                    .setParameter("sender", sender)
                    .setParameter("receiver", receiver)
                    .getResultList();
        } finally {
            em.close();
        }
    }



    public void saveMessage(MessageDTO dto) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Message msg = new Message();
            msg.setContent(dto.getContent());
            msg.setSender(dto.getSender());
            msg.setReceiver(dto.getReceiver());
            msg.setSent_at(dto.getTimestamp());
            Customer customer = session.get(Customer.class, dto.getCustomerID());
            msg.setCustomer(customer); // 👈 gán FK customer

            session.save(msg);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

}
