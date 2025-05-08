package service;

import repositoy_dao.DAOInterface;

import java.util.List;

public abstract class AbstractService<T, ID> implements ServiceInterface<T, ID> {
    protected DAOInterface<T, ID> dao;

    @Override
    public boolean insert(T t) {
        return dao.insert(t);
    }

    @Override
    public boolean update(T t) {
        return dao.update(t);
    }

    @Override
    public boolean delete(T t) {
        return dao.delete(t);
    }

    @Override
    public T findById(ID id) {
        return dao.selecById((int) id);
    }

    @Override
    public List<T> findAll() {
        return dao.selectAll();
    }
}
