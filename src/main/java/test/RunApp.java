package test;

import model.Customer;
import model.MenuItem;
import model.TableBooking;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import util.HibernateUtil;

import java.math.BigDecimal;

public class RunApp {
    public static void main(String[] args) {
        try{
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();
            session.beginTransaction();

//            TableBooking tb = new TableBooking();
//            tb.setTableType(TableBooking.TableType.valueOf("BAN"));
//            tb.setStatus(TableBooking.StatusTable.valueOf("TRONG"));
//            session.save(tb);
            Customer c = new Customer("tanloi", "123", "0398990559");
            session.save(c);
//            MenuItem menuItem = new MenuItem();
//            menuItem.setPrice(BigDecimal.valueOf(200000));
//            menuItem.setQuantity(50);
//            menuItem.setName("Tom Hum");
//            session.save(menuItem);

            session.getTransaction().commit();
            session.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
