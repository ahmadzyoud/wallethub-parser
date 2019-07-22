package com.ef;

import com.ef.domain.AccessIpStatistics;
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
            System.out.println("88888888888888888888888888888888888888888888888888888888888 = " + System.nanoTime());
            accessLogService.insert(command);
            System.out.println("77777777777777777777777777777777777777777777777777777777777777777 = " + System.nanoTime());
        }
        System.out.println("BBBBBBBBBBBBBB = " + System.nanoTime());
        List<AccessIpStatistics> result = accessLogService.search(command);

        result.forEach(System.out::println);


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
