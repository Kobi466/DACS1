package repositoy_dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import model.Customer;
import org.hibernate.Session;

import util.HibernateUtil;

public  class CustomerDAO extends AbstractDAO<Customer, Integer> {
    public static CustomerDAO getInstance(){
        return new CustomerDAO();
    }
    public CustomerDAO() {
        super(Customer.class);
    }

    public Customer findByPhone(String phone) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Customer c WHERE c.sdt = :phone", Customer.class)
                    .setParameter("phone", phone)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // hoặc throw exception tùy bạn xử lý
        }
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
