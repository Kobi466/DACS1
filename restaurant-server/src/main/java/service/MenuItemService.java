package service;

import model.MenuItem;
import repositoy_dao.MenuItemDAO;

import java.util.List;
import java.util.stream.Collectors;

public class MenuItemService extends AbstractService<model.MenuItem, Integer> {
    public MenuItemService() {
        this.dao = new repositoy_dao.MenuItemDAO();
    }
    public List<String> showmenu(){
        List<MenuItem> menuItems = MenuItemDAO.getInstance().selectAll();
        List<String> menuList = menuItems.stream()
                .map(item -> "- " + item.getName() + ": " + item.getPrice() + " VNĐ")
                .collect(Collectors.toList());
        return menuList;
    }
}
