package com.ef.service;

import com.ef.domain.AccessLog;
import com.ef.model.Command;
import com.ef.repository.AccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AccessLogServiceImpl implements AccessLogService {

    private AccessLogRepository accessLogRepository;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private Integer recordCounts = 10000;

    private Integer batchSize;


    public AccessLogRepository getAccessLogRepository() {
        return accessLogRepository;
    }

    @Autowired
    public void setAccessLogRepository(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    @Override
    public void insert(Command command) {
        String accessLogPath = command.getAccessLogPath();

        FileInputStream accessLogStream = null;
        try {
            accessLogStream = new FileInputStream(accessLogPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File " + accessLogPath + " is not exist");
        }

        Scanner accessLogScanner = new Scanner(accessLogStream);
        List<AccessLog> list = new ArrayList<>();
        Integer threadIndex = 0;
        while (accessLogScanner.hasNext()) {
            String nextLine = accessLogScanner.nextLine();
            String[] data = nextLine.split("\\|");
            AccessLog accessLog = new AccessLog();
            accessLog.setAccessDate(parseDate(data[0]));
            accessLog.setIp(data[1]);
            accessLog.setRequest(data[2]);
            accessLog.setStatus(Integer.valueOf(data[3]));
            accessLog.setUserAgent(data[4]);
            list.add(accessLog);

            if (list.size() > recordCounts) {

                constructThread(list, threadIndex.toString());
                threadIndex++;
                list = new ArrayList<>();


            }

        }
        constructThread(list, threadIndex.toString());
        executorService.shutdown();
    }

    @Override
    public List<AccessLog> search(Command command) {
        return null;
    }


    private void constructThread(List<AccessLog> list, String threadName) {
        executorService.submit(
                () -> {
                    String name = Thread.currentThread().getName();

                    Instant start = Instant.now();
                    System.out.println(name + " Thread Start " + new Date());
                    accessLogRepository.saveAll(list);
                    accessLogRepository.flush();

                    System.out.println(name + " Thread Finish " + new Date());
                    Instant finish = Instant.now();

                    long timeElapsed = Duration.between(start, finish).toMillis();
                    System.out.println("timeElapsed " + name + " = " + timeElapsed);
                });

    }

    private LocalDateTime parseDate(String accessDate) {
        String dateTimePattern = "yyyy-MM-dd HH:mm:ss.SSS";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

        return LocalDateTime.parse(accessDate, dateFormatter);
    }


}
