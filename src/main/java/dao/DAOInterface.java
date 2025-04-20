package dao;

import java.util.List;

public interface DAOInterface<T, ID>{
    public void insert(T t);
    public void update(T t);
    public void delete(T t);
    public List<T> selectAll();
    public T selecById(int id);

}
