package repositoy_dao;

import model.Customer;
import org.hibernate.Session;
import util.HibernateUtil;

public class CustomerDAO extends AbstractDAO<Customer, Integer> {
    public static CustomerDAO getInstance(){
        return new CustomerDAO();
    }
    public CustomerDAO() {
        super(Customer.class);
    }
    public Customer findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Customer WHERE userName = :username", Customer.class)
                    .setParameter("username", username)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public int findCustomerIdByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Customer customer = session.createQuery("FROM Customer WHERE userName = :username", Customer.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return customer != null ? customer.getCustomer_Id() : -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Trả về giá trị không hợp lệ nếu không tìm thấy
        }
    }

}
