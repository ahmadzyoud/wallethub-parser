package com.ef.service;

import com.ef.domain.AccessIpStatistics;
import com.ef.model.Command;

import java.util.List;

public interface AccessLogService {

    void insert(Command command);

    List<AccessIpStatistics> search(Command command);

}
