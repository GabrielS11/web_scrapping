package gvv.Repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> implements PDIRepository<T, ID> {

    private final EntityManager entityManager = Persistence.createEntityManagerFactory("default").createEntityManager();
    private final Class<T> entityType;

    public BaseRepository(Class<T> entityType) {
        this.entityType = entityType;
    }

    @Override
    public T save(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            T savedEntity = entityManager.merge(entity);
            if (isNewTransaction) {
                transaction.commit();
            }
            return savedEntity;
        } catch (Exception e) {
            if (isNewTransaction && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }


    @Override
    public Optional<T> findById(ID id) {
        T entity = entityManager.find(entityType, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<T> findAll() {
        return entityManager.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType).getResultList();
    }

    @Override
    @Transactional
    public T update(T entity) {
        return save(entity);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        T entity = entityManager.find(entityType, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
