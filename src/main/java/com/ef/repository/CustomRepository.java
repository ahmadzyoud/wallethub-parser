package com.ef.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;


@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {
    <S extends T> void saveAll(List<S> entities);
}
