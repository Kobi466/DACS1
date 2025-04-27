package vn.kobi;

import model.*;
import org.hibernate.Session;
import util.HibernateUtil;
import view.customerView.CustomerGUI;

import javax.swing.*;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            System.out.println("\n--- Customers ---");
            session.createQuery("from Customer", Customer.class).list().forEach(System.out::println);

            System.out.println("\n--- Orders ---");
            session.createQuery("from Order", Order.class).list().forEach(System.out::println);

            System.out.println("\n--- Menu Items ---");
            session.createQuery("from MenuItem", MenuItem.class).list().forEach(System.out::println);

            System.out.println("\n--- Order Items ---");
            session.createQuery("from OrderItem", OrderItem.class).list().forEach(System.out::println);

            System.out.println("\n--- Ratings ---");
            session.createQuery("from Rating", Rating.class).list().forEach(System.out::println);

            System.out.println("\n--- Table Bookings ---");
            session.createQuery("from TableBooking", TableBooking.class).list().forEach(System.out::println);

            System.out.println("\n--- Reservations ---");
            session.createQuery("from Reservation", Reservation.class).list().forEach(System.out::println);

            System.out.println("\n--- Vouchers ---");
            session.createQuery("from Voucher", Voucher.class).list().forEach(System.out::println);

            System.out.println("\n--- Customer Vouchers ---");
            session.createQuery("from CustomerVoucher", CustomerVoucher.class).list().forEach(System.out::println);

            System.out.println("\n--- Kitchen Queue ---");
            session.createQuery("from KitchenQueue", KitchenQueue.class).list().forEach(System.out::println);

            System.out.println("\n--- Messages ---");
            session.createQuery("from Message", Message.class).list().forEach(System.out::println);

            session.getTransaction().commit();
        }
    }
}

