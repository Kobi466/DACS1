package service;

public class RatingService extends AbstractService<model.Rating, Integer> {
    public RatingService() {
        this.dao = new repositoy_dao.RatingDAO();
    }
}
