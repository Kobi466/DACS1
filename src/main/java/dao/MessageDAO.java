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

    public void save(MessageDTO dto) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Message message = new Message();
            message.setSender(dto.getSender());
            message.setReceiver(dto.getReceiver());
            message.setContent(dto.getContent());
            message.setSent_at(dto.getTimestamp());
            Customer customer = session.get(Customer.class, dto.getCustomerID());
            message.setCustomer(customer);

            session.persist(message);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
