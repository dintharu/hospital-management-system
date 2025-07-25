package repository;

import java.sql.SQLException;
import java.util.List;

public interface CRUDRepository <T,ID> extends SuperRepository{
    boolean add(T dao) throws SQLException;

    boolean delete(String name) throws SQLException;

    T searchByName(String name) throws SQLException;  // instead of boolean

    List<T> getAll() throws SQLException;

}
