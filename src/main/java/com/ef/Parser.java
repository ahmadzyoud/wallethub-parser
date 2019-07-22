package com.ef;

import com.ef.domain.AccessLog;
import com.ef.model.Command;
import com.ef.repository.CustomRepositoryImpl;
import com.ef.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
public class Parser implements CommandLineRunner {

    private AccessLogService accessLogService;


    public static void main(String[] args) {
        SpringApplication.run(Parser.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Instant start = Instant.now();
        Command command = new Command(args);
        if (command.isContainInsertComand()) {
            accessLogService.insert(command);
        }

        List<AccessLog> result =  accessLogService.search(command);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("ZZZZZZZZZZZZZZZ = " + System.nanoTime());
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).getSeconds();
            System.out.println("timeElapsed = " + timeElapsed);
        }));


    }

    public AccessLogService getAccessLogService() {
        return accessLogService;
    }

    @Autowired
    public void setAccessLogService(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }
}
