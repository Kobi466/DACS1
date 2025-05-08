package repositoy_dao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import util.HibernateUtil;
import java.util.List;
public abstract class AbstractDAO<T, ID> implements DAOInterface<T, ID> {
    private final Class clazz;
    public AbstractDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean insert(T t) {
        EntityManager em = HibernateUtil.getSessionFactory().openSession();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (em.contains(t)) {
                em.persist(t); // Chỉ persist nếu entity chưa tồn tại trong context
            } else {
                em.merge(t); // Merge nếu entity đã detached hoặc có ID
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean update(T t) {
        EntityManager em = HibernateUtil.getSessionFactory().openSession();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(t);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(T t) {
        EntityManager em = HibernateUtil.getSessionFactory().openSession();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.remove(em.contains(t) ? t : em.merge(t));
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> selectAll() {
        EntityManager em = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "from " + clazz.getSimpleName();
            TypedQuery<T> query = em.createQuery(hql, clazz);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public T selecById(int id) {
        EntityManager em = HibernateUtil.getSessionFactory().openSession();
        try {
            return (T) em.find(clazz, id);
        } finally {
            em.close();
        }
    }
}
