package repositoy_dao;

import model.TableBooking;

public class TableBookingDAO extends AbstractDAO<TableBooking, Integer> {
    public TableBookingDAO() {
        super(TableBooking.class);
    }
    public static TableBookingDAO getInstance(){
        return new TableBookingDAO();
    }
}
