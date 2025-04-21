package dao;

import model.Customer;

public class CustomerDAO extends AbstractDAO<Customer, Integer> {
    public static CustomerDAO getInstance(){
        return new CustomerDAO();
    }
    public CustomerDAO() {
        super(Customer.class);
    }
    public Customer findByUsername(String username, String password) {
        return em.createQuery("from Customer where userName =:username and password =: password", Customer.class).setParameter("username", username).setParameter("password", password).getSingleResult();
    }
}
