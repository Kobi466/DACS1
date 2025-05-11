package vn.kobi;

import model.MenuItem;
import repositoy_dao.MenuItemDAO;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Com");
        menuItem.setPrice(BigDecimal.valueOf(10000));
        menuItem.setQuantity(5);
        MenuItemDAO.getInstance().insert(menuItem);
    }}
