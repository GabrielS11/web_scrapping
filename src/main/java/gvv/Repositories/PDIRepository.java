package gvv.Repositories;

import java.util.List;
import java.util.Optional;

public interface PDIRepository<T, ID> {
    T  save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    T  update(T entity);
    void deleteById(ID id);
    T findOrCreate(String parameter, String value, T entity);
    void initializeCache(String parameter);
    void close();

}
