package service;

public class KitchenQueueService extends AbstractService<model.KitchenQueue, Integer> {
    public KitchenQueueService() {
        this.dao = new repositoy_dao.KitchenQueueDAO();
    }
}
