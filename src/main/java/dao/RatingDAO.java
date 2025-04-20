package dao;

import model.Customer;
import model.Rating;

import java.util.List;

public class RatingDAO extends AbstractDAO<Rating, Integer> {
    public static RatingDAO getInstance(){
        return new RatingDAO();
    }
    public RatingDAO() {
        super(Rating.class);
    }
    public List<Rating> findByCustomer(Customer cusTomer) {
        return em.createQuery("from Rating where customer =: cusTomer", Rating.class).setParameter("cusTomer", cusTomer).getResultList();
    }
}
