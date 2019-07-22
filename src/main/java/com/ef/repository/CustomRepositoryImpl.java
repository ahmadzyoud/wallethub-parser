package com.ef.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

@NoRepositoryBean
public class CustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CustomRepository<T, ID> {

    @PersistenceContext
    private final EntityManager entityManager;


    public CustomRepositoryImpl(JpaEntityInformation entityInformation,
                                EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public <S extends T> void saveAll(List<S> entities) {


        int entityCount = entities.size();
        int batchSize = 100;
        try {
            Iterator<S> iterator = entities.iterator();
            for (int i = 0; i < entityCount; i++) {
                if (i > 0 && i % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
                entityManager.persist(iterator.next());
            }

        } catch (RuntimeException e) {
            throw e;
        } finally {
            entityManager.close();
        }
    }


}
