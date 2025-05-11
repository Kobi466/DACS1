package repositoy_dao;

import dto.CustomerDTO;
import dto.MessageDTO;
import model.Customer;
import model.Message;
import util.HibernateUtil;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO extends AbstractDAO<Message, Integer> implements DAOInterface<Message, Integer>{
    public MessageDAO() {
        super(Message.class);
    }
    public static MessageDAO getInstance(){
        return new MessageDAO();
    }
    public static boolean insertMessage(MessageDTO messageDTO) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Customer customer = session.get(Customer.class, messageDTO.getCustomerId());
            // Convert DTO to Entity
            Message message = new Message();
            message.setContent(messageDTO.getContent());
            message.setSender(messageDTO.getSender());
            message.setReceiver(messageDTO.getReceiver());
            message.setSent_at(messageDTO.getSentAt() != null ? LocalDateTime.parse(messageDTO.getSentAt()) : LocalDateTime.now());
            message.setCustomer(customer);

            session.save(message); // Save message vào DB
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public static List getMessages(String customerUsername, String staffUsername) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery(MessageDTO.class);
            Root root = query.from(Message.class);
            // Chỉ lấy các trường cần thiết để tạo MessageDTO
            query.select(cb.construct(
                    MessageDTO.class,
                    root.get("sender"),
                    root.get("receiver"),
                    root.get("content"),
                    root.get("sent_at"),
                    root.get("customer").get("customer_Id")
            ));

            Predicate condition1 = cb.and(cb.equal(root.get("sender"), customerUsername), cb.equal(root.get("receiver"), staffUsername));
            Predicate condition2 = cb.and(cb.equal(root.get("sender"), staffUsername), cb.equal(root.get("receiver"), customerUsername));
            query.where(cb.or(condition1, condition2));
            query.orderBy(cb.asc(root.get("sent_at"))); // Order by sent_at

            // Trả kết quả trực tiếp dưới dạng MessageDTO
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    public static List<CustomerDTO> getCustomersWithMessages() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT m.customer FROM Message m";
            List<Customer> customers = session.createQuery(hql, Customer.class).getResultList();

            List<CustomerDTO> customerDTOList = new ArrayList<>();
            for (Customer customer : customers) {
                CustomerDTO dto = new CustomerDTO();
                dto.setUserName(customer.getUserName());
                customerDTOList.add(dto);
            }
            return customerDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static List<MessageDTO> getCustomersWithLastMessage() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = """
                SELECT new dto.MessageDTO(
                    m.sender, 
                    m.receiver, 
                    m.content, 
                    m.sent_at, 
                    m.customer.customer_Id
                ) 
                FROM Message m
                WHERE m.sent_at = (
                    SELECT MAX(m2.sent_at) 
                    FROM Message m2 
                    WHERE m2.customer.customer_Id = m.customer.customer_Id
                )
                ORDER BY m.sent_at DESC
                """;

            // Truy vấn tất cả khách hàng kèm tin nhắn gần nhất
            List<MessageDTO> messages = session.createQuery(hql, MessageDTO.class).getResultList();
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
