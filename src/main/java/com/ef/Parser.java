package com.ef;

import com.ef.domain.AccessLog;
import com.ef.model.Command;
import com.ef.repository.AccessLogRepository;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

@SpringBootApplication
public class Parser implements CommandLineRunner {

    @Autowired
    private AccessLogRepository accessLogRepository;



    public static void main(String[] args) {
        SpringApplication.run(Parser.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Instant start = Instant.now();
        manualThread(args, 10000);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).getSeconds();
            System.out.println("timeElapsed = " + timeElapsed);
        }));

    }

    private void manualThread(String[] args, int batchSize) throws ParseException, FileNotFoundException {

        //ExecutorService executorService = Executors.newFixedThreadPool(5);

        Command command = new Command(args);

        String accessLogPath = command.getAccessLogPath();

        FileInputStream accessLogStream = new FileInputStream(accessLogPath);

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

            if (list.size() > batchSize) {

                constructThread(list, threadIndex.toString());
                threadIndex++;
                list = new ArrayList<>();


            }

        }
        constructThread(list, threadIndex.toString());
    }

    private LocalDateTime parseDate(String accessDate) {
        String dateTimePattern = "yyyy-MM-dd HH:mm:ss.SSS";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

        return LocalDateTime.parse(accessDate, dateFormatter);
    }

    private void constructThread(List<AccessLog> list, String threadName) {
        new Thread(() -> {
            String name = Thread.currentThread().getName();

            Instant start = Instant.now();
            System.out.println(name + " Thread Start " + new Date());
            accessLogRepository.saveAll(list);
            System.out.println(name + " Thread Finish " + new Date());
            Instant finish = Instant.now();

            long timeElapsed = Duration.between(start, finish).toMillis();
            System.out.println("timeElapsed " + name + " = " + timeElapsed);
        }, threadName).start();

    }
}
