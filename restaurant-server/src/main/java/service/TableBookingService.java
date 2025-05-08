package service;

public class TableBookingService extends AbstractService<model.TableBooking, Integer> {
    public TableBookingService() {
        this.dao = new repositoy_dao.TableBookingDAO();
    }
}
