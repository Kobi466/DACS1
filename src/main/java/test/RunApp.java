package test;

import model.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import util.HibernateUtil;

public class RunApp {
    public static void main(String[] args) {
        Customer c = new Customer();
        c.setPassword("password23");
        c.setUserName("user123");
        c.setSdt("0398990559");
        try{
            SessionFactory sf = new HibernateUtil().getSessionFactory();
            if (sf != null){
                Session session = sf.openSession();
                session.beginTransaction();
                session.save(c);
                session.getTransaction().commit();
                session.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
