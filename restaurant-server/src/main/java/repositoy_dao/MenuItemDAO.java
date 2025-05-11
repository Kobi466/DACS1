package repositoy_dao;

import model.MenuItem;
import org.hibernate.Session;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

public class MenuItemDAO extends AbstractDAO<MenuItem, Integer>{
    private Session session;
    public static MenuItemDAO getInstance(){
        return new MenuItemDAO();
    }
    public MenuItemDAO() {
        super(MenuItem.class);
    }
    public MenuItem findByName(String name){
        // Implement the logic to find a MenuItem by its name
        // This method should return a MenuItem object if found, or null if not found
        // Example:
        try{
            SessionFactory sessionBuilder = HibernateUtil.getSessionFactory();
            if (sessionBuilder != null){
                session = sessionBuilder.openSession();
                Transaction transaction = session.beginTransaction();
                Query<MenuItem> query = session.createQuery("FROM MenuItem WHERE name = :name", MenuItem.class);
                query.setParameter("name", name);
                MenuItem menuItem = query.uniqueResult();
                transaction.commit();
                session.close();
                return menuItem;

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }
}
