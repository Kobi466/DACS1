package repositoy_dao;

import model.KitchenQueue;

public class KitchenQueueDAO extends AbstractDAO<KitchenQueue, Integer> {
    public static KitchenQueueDAO getInstance(){
         return new KitchenQueueDAO();
     }
    public KitchenQueueDAO() {
        super(KitchenQueue.class);
    }
}
