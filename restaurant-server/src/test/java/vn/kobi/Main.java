package vn.kobi;

import model.Customer;
import model.MenuItem;
import model.TableBooking;
import repositoy_dao.CustomerDAO;
import repositoy_dao.MenuItemDAO;
import repositoy_dao.TableBookingDAO;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Súp cua tổ yến");
        menuItem.setPrice(BigDecimal.valueOf(320.000));
        menuItem.setQuantity(1);
        MenuItemDAO.getInstance().insert(menuItem);
        MenuItem menuItem1 = new MenuItem();
        menuItem.setName("Salad cá hồi hun khói");
        menuItem.setPrice(BigDecimal.valueOf(280.000));
        menuItem.setQuantity(1);
        MenuItemDAO.getInstance().insert(menuItem1);
        MenuItem menuItem2 = new MenuItem();
        menuItem.setName("Bò Wagyu nướng đá");
        menuItem.setPrice(BigDecimal.valueOf(890.000));
        menuItem.setQuantity(1);
        MenuItemDAO.getInstance().insert(menuItem2);
        MenuItem menuItem3 = new MenuItem();
        menuItem.setName("Gan ngỗng áp chảo");
        menuItem.setPrice(BigDecimal.valueOf(280.000));
        menuItem.setQuantity(1);
        MenuItemDAO.getInstance().insert(menuItem3);
        MenuItem menuItem4 = new MenuItem();
        menuItem.setName("Vịt nướng sốt cam");
        menuItem.setPrice(BigDecimal.valueOf(520.000));
        menuItem.setQuantity(1);
        MenuItemDAO.getInstance().insert(menuItem4);
        MenuItem menuItem5 = new MenuItem();
        menuItem.setName("Tôm hùm bỏ lò phô mai");
        menuItem.setPrice(BigDecimal.valueOf(1200.000));
        menuItem.setQuantity(1);
        MenuItemDAO.getInstance().insert(menuItem5);
        MenuItem menuItem6 = new MenuItem();
        menuItem.setName("Cá chẽm sốt bơ chanh");
        menuItem.setPrice(BigDecimal.valueOf(480.000));
        menuItem.setQuantity(1);
        MenuItemDAO.getInstance().insert(menuItem6);
        MenuItem menuItem7 = new MenuItem();
        menuItem.setName("Rượu vang đỏ Chile");
        menuItem.setPrice(BigDecimal.valueOf(190,000));
        menuItem.setQuantity(1);
        MenuItemDAO.getInstance().insert(menuItem7);



//        Customer customer = new Customer();Rượu vang đỏ Chile
//        customer.setUserName("kobi");
//        customer.setSdt("0366500642");
//        customer.setPassword("123");
//        CustomerDAO.getInstance().insert(customer);
//        for(int i = 1; i <= 8; i++){
//            TableBooking tableBooking = new TableBooking();
//            tableBooking.setTableName("BAN" + i);
//            tableBooking.setTableType(TableBooking.TableType.BAN);
//            tableBooking.setStatus(TableBooking.StatusTable.TRONG);
//            TableBookingDAO.getInstance().insert(tableBooking);
//        }
//        for(int i = 1; i <= 6; i++){
//            TableBooking tableBooking = new TableBooking();
//            tableBooking.setTableName("PHONGVIP" + i);
//            tableBooking.setTableType(TableBooking.TableType.PHONG_VIP);
//            tableBooking.setStatus(TableBooking.StatusTable.TRONG);
//            TableBookingDAO.getInstance().insert(tableBooking);
//        }

    }}
