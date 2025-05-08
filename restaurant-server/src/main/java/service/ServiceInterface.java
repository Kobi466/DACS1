package service;

import java.util.List;

public interface ServiceInterface<T, ID> {
    boolean insert(T t);
    boolean update(T t);
    boolean delete(T t);
    T findById(ID id);
    List<T> findAll();
}

