package com.ef.repository;

import com.ef.domain.BlockedIp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedIpRepository extends CrudRepository<BlockedIp, Long> {
}
