package dao;

import model.MenuItem;

import java.util.List;

public class MenuItemDAO extends AbstractDAO<MenuItem, Integer>{
    public static MenuItemDAO getInstance(){
        return new MenuItemDAO();
    }
    public MenuItemDAO() {
        super(MenuItem.class);
    }
    public List<MenuItem> findAvailable() {
        return em.createQuery("from MenuItem where quantity > 0 ", MenuItem.class).getResultList();
    }
}
