package com.ef;

import com.ef.domain.AccessLog;
import com.ef.model.Command;
import com.ef.repository.AccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class Parser implements CommandLineRunner {

    @Autowired
    private AccessLogRepository accessLogRepository;


    public static void main(String[] args) {
        SpringApplication.run(Parser.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Command command = new Command(args);

        String accessLogPath = command.getAccessLogPath();

        FileInputStream accessLogStream = new FileInputStream(accessLogPath);

        Scanner accessLogScanner = new Scanner(accessLogStream);
        accessLogRepository.deleteAllInBatch();
        int coun = 0;
        List<AccessLog> list = new ArrayList<>();
        Instant start = Instant.now();

        while (accessLogScanner.hasNext()) {
            String nextLine = accessLogScanner.nextLine();
            coun++;
            String[] data = nextLine.split("\\|");
            AccessLog accessLog = new AccessLog();

            accessLog.setAccessDate(parseDate(data[0]));
            accessLog.setIp(data[1]);
            accessLog.setRequest(data[2]);
            accessLog.setStatus(Integer.valueOf(data[3]));
            accessLog.setUserAgent(data[4]);
            list.add(accessLog);

            if (list.size() > 10000) {
                constructThread(list);
                list = new ArrayList<>();


            }

        }
        constructThread(list);
        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start, finish).getSeconds();  //in millis
        System.out.println("timeElapsed = " + timeElapsed);
    }

    private LocalDateTime parseDate(String accessDate) {
        String dateTimePattern = "yyyy-MM-dd HH:mm:ss.SSS";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

        return LocalDateTime.parse(accessDate, dateFormatter);
    }

    private void constructThread(List<AccessLog> list) {
        new Thread(() ->
                accessLogRepository.saveAll(list)).start();

    }
}
