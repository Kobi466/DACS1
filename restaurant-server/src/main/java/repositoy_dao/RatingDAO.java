package repositoy_dao;

import model.Rating;

public class RatingDAO extends AbstractDAO<Rating, Integer> {
    public static RatingDAO getInstance(){
        return new RatingDAO();
    }
    public RatingDAO() {
        super(Rating.class);
    }
}
