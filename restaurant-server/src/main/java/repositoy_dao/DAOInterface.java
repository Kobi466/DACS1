package repositoy_dao;

import java.util.List;

public interface DAOInterface<T, ID>{
    public boolean insert(T t);
    public boolean update(T t);
    public boolean delete(T t);
    public List<T> selectAll();
    public T selecById(int id);

}
