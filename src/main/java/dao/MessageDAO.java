package dao;

import model.Customer;
import model.Message;

import java.time.LocalDateTime;
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

    public void insert(Customer currentCustomer, String customer, String text, LocalDateTime timestamp) {

    }
}
