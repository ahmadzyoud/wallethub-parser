package com.ef.repository;

import com.ef.domain.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccessLogRepository extends CrudRepository<AccessLog, Long> , JpaRepository<AccessLog, Long> {

}
