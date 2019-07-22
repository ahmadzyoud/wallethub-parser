package com.ef.repository;

import com.ef.domain.AccessIpStatistics;
import com.ef.domain.AccessLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AccessLogRepository extends CustomRepository<AccessLog, Long> {

    @Query("SELECT " +
            "new com.ef.domain.AccessIpStatistics(al.ip, COUNT(al)) " +
            "FROM AccessLog al " +
            "WHERE al.accessDate >= :startDate " +
            "AND al.accessDate <= :endDate " +
            "GROUP BY al.ip " +
            "HAVING COUNT(al) >= :threshold ")
    List<AccessIpStatistics> findOverThresholdIp(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                                 @Param("threshold") Long threshold);
}
