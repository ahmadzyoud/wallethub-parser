package com.ef.service;

import com.ef.domain.AccessLog;
import com.ef.model.Command;

import java.util.List;

public interface AccessLogService {

    void insert(Command command);

    List<AccessLog> search(Command command);
}
