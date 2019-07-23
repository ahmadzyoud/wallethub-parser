package com.ef.service;

import com.ef.domain.AccessIpStatistics;
import com.ef.domain.AccessLog;
import com.ef.domain.BlockedIp;
import com.ef.model.Command;
import com.ef.model.enumeration.Duration;
import com.ef.repository.AccessLogRepository;
import com.ef.repository.BlockedIpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

;

@Service
public class AccessLogServiceImpl implements AccessLogService {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private AccessLogRepository accessLogRepository;

    private BlockedIpRepository blockedIpRepository;

    private ExecutorService executorService = Executors.newFixedThreadPool(11);

    private Integer recordCounts = 10000;


    public AccessLogRepository getAccessLogRepository() {
        return accessLogRepository;
    }

    @Autowired
    public void setAccessLogRepository(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    public BlockedIpRepository getBlockedIpRepository() {
        return blockedIpRepository;
    }

    @Autowired
    public void setBlockedIpRepository(BlockedIpRepository blockedIpRepository) {
        this.blockedIpRepository = blockedIpRepository;
    }

    @Override
    @Transactional
    public void insert(Command command) {
        String accessLogPath = command.getAccessLogPath();

        FileInputStream accessLogStream;
        try {
            accessLogStream = new FileInputStream(accessLogPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File " + accessLogPath + " is not exist");
        }

        long initialValue = 1;
        AccessLog lastAccessLog = accessLogRepository.findFirstByOrderByIdDesc();
        if (lastAccessLog != null) {
            initialValue = lastAccessLog.getId() + 1;
        }

        Scanner accessLogScanner = new Scanner(accessLogStream);
        List<AccessLog> list = new ArrayList<>();

        AtomicLong atomicLong = new AtomicLong(initialValue);
        while (accessLogScanner.hasNext()) {
            String nextLine = accessLogScanner.nextLine();
            String[] data = nextLine.split("\\|");
            AccessLog accessLog = new AccessLog();
            accessLog.setId(atomicLong.getAndIncrement());
            accessLog.setAccessDate(parseDate(data[0]));
            accessLog.setIp(data[1]);
            accessLog.setRequest(data[2]);
            accessLog.setStatus(Integer.valueOf(data[3]));
            accessLog.setUserAgent(data[4]);
            list.add(accessLog);
        }


        List<MyCallable> futureList = new ArrayList<>();
        for (int i = 0; i < list.size(); i += recordCounts) {

            List<AccessLog> bulk = list.subList(i, (i + recordCounts) < (list.size() - 1) ? (i + recordCounts) : list.size());
            MyCallable callable = constructCallable(bulk);
            futureList.add(callable);

        }
        try {
            List<Future<Long>> futures = executorService.invokeAll(futureList);
        } catch (Exception err) {
            err.printStackTrace();
        }


        executorService.shutdown();
    }

    @Override
    public List<AccessIpStatistics> search(Command command) {

        LocalDateTime startDate = command.getStartDate();
        Duration duration = command.getDuration();
        Integer threshold = command.getThreshold();
        LocalDateTime endDate = startDate.plusHours(1);
        if (Duration.DAILY.equals(duration)) {
            endDate = startDate.plusDays(1);
        }


        List<AccessIpStatistics> result = accessLogRepository.findOverThresholdIp(convertToDateViaSqlTimestamp(startDate), convertToDateViaSqlTimestamp(endDate), threshold.longValue());

        if (result != null && !result.isEmpty()) {
            List<BlockedIp> blockedIpList = result.stream().map(each -> {
                BlockedIp blockedIp = new BlockedIp();
                blockedIp.setIp(each.getIp());
                blockedIp.setAccessCount(each.getIpCount());
                blockedIp.setBlockReason("The IP accessed more than " + command.getThreshold() + " " + command.getDuration().toString());
                return blockedIp;
            }).collect(Collectors.toList());

            blockedIpRepository.saveAll(blockedIpList);

        }


        return result;
    }

    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }


    private MyCallable constructCallable(List<AccessLog> list) {
        return new MyCallable(list);
    }

    class MyCallable implements Callable<Long> {
        private List<AccessLog> list;

        public MyCallable(List<AccessLog> list) {
            this.list = list;
        }

        public Long call() {
            accessLogRepository.saveAll(list);
            return null;
        }
    }


    private Date parseDate(String accessDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            return simpleDateFormat.parse(accessDate);
        } catch (ParseException ex) {
            throw new RuntimeException("Wrong access date format : " + accessDate + " Date format : " + DATE_FORMAT);
        }
    }


}
