package service;

public class MenuItemService extends AbstractService<model.MenuItem, Integer> {
    public MenuItemService() {
        this.dao = new repositoy_dao.MenuItemDAO();
    }
}
