package gvv.Repositories;

import gvv.WebScrappingOptimized.DatabaseHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseRepository<T, ID> implements PDIRepository<T, ID> {


    private final Class<T> entityType;
    private final Map<String, T> cache = new HashMap<>();

    public BaseRepository(Class<T> entityType) {
        this.entityType = entityType;
    }

    protected EntityManager getEntityManager() {
        return DatabaseHandler.getEntityManager();
    }

    @Override
    public T save(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        boolean isNewTransaction = !transaction.isActive();
        try {
            if (isNewTransaction) {
                transaction.begin();
            }
            T savedEntity = getEntityManager().merge(entity);
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
        EntityManager em = getEntityManager();
        T entity = em.find(entityType, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        List<T> ret = em.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType).getResultList();
        return ret;
    }

    @Override
    public T update(T entity) {
        return save(entity);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        EntityManager em = getEntityManager();
        T entity = em.find(entityType, id);
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    public final T findOrCreate(String parameter, String value, T entity) {
        T cachedEntity = cache.get(value);
        if (cachedEntity != null) {
            return cachedEntity;
        }
        EntityManager em = getEntityManager();
        String query = "SELECT e FROM " + entityType.getSimpleName() + " e WHERE e." + parameter + " = :value";
        T existingEntity = em.createQuery(query, entityType)
                .setParameter("value", value)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if (existingEntity != null) {
            return existingEntity;
        }
        cache.put(value, save(entity));
        return cache.get(value);
    }

    @Override
    public final void initializeCache(String parameter) {
        EntityManager em = getEntityManager();
        String query = "SELECT e FROM " + entityType.getSimpleName() + " e";
        List<T> entities = em.createQuery(query, entityType).getResultList();

        for (T entity : entities) {
            try {
                String value = entityType.getDeclaredField(parameter).get(entity).toString();
                cache.put(value, entity);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {

            }
        }
    }

    @Override
    public void close() {

    }
}
