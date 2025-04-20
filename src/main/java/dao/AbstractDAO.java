package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import util.HibernateUtil;

import java.util.List;

public class AbstractDAO<T, ID> implements DAOInterface<T, ID>{
    protected EntityManager em;
    private final Class<T> clazz;
    public AbstractDAO(Class<T> clazz) {
        this.clazz = clazz;
        this.em = HibernateUtil.getSessionFactory().openSession();
    }
    @Override
    public void insert(T t) {
        em.getTransaction().begin();
        em.persist(t);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void update(T t) {
        em.getTransaction().begin();
        em.merge(t);
        em.getTransaction().commit();
    }

    @Override
    public void delete(T t) {
        em.getTransaction().begin();
        em.remove(em.contains(t) ? t : em.merge(t));
        em.getTransaction().commit();
    }

    @Override
    public List<T> selectAll() {
        TypedQuery<T> query = em.createQuery("from " + clazz.getSimpleName(), clazz);
        return query.getResultList();
    }

    @Override
    public T selecById(int id) {
        return em.find(clazz, id);
    }
}
