package dao;

import model.KitchenQueue;

import java.util.List;

public class KitchenQueueDAO extends AbstractDAO<KitchenQueue, Integer> {
     public static KitchenQueueDAO getInstance(){
         return new KitchenQueueDAO();
     }
    public KitchenQueueDAO() {
        super(KitchenQueue.class);
    }
    public List<KitchenQueue> findPending() {
        return em.createQuery("from KitchenQueue where status = 'ĐangNấu'", KitchenQueue.class).getResultList();
    }
}
