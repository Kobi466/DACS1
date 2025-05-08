package repositoy_dao;

import model.MenuItem;

public class MenuItemDAO extends AbstractDAO<MenuItem, Integer>{
    public static MenuItemDAO getInstance(){
        return new MenuItemDAO();
    }
    public MenuItemDAO() {
        super(MenuItem.class);
    }
}
