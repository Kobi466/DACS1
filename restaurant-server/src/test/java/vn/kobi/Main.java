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
        menuItem.setName("Com");
        menuItem.setPrice(BigDecimal.valueOf(10000));
        menuItem.setQuantity(5);
        MenuItemDAO.getInstance().insert(menuItem);
//        Customer customer = new Customer();
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
