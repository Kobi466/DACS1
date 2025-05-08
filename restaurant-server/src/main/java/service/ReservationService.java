package service;

public class ReservationService extends AbstractService<model.Reservation, Integer> {
    public ReservationService() {
        this.dao = new repositoy_dao.ReservationDAO();
    }
}
