package repositoy_dao;

import model.Reservation;

public class ReservationDAO extends AbstractDAO<Reservation, Integer> {
    public static ReservationDAO getInstance(){
        return new ReservationDAO();
    }
    public ReservationDAO() {
        super(Reservation.class);
    }
}