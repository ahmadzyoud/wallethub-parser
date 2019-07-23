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

import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
public class Parser implements CommandLineRunner {

    private AccessLogService accessLogService;


    public static void main(String[] args) {
        SpringApplication.run(Parser.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            Command command = new Command(args);
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            if (command.isContainInsertComand()) {
                accessLogService.insert(command);
            }

            List<AccessIpStatistics> result = accessLogService.search(command);
            if (result == null || result.isEmpty()) {
                System.out.println("--------------------------------------------------------");
                System.out.println("--------------------------------------------------------");
                System.out.println("No results have been found");
                System.out.println("--------------------------------------------------------");
                System.out.println("--------------------------------------------------------");
                return;
            }
            System.out.println("---------------------------------------");
            System.out.println("|--------IP---------|------COUNT------|");
            result.forEach(each -> {
                System.out.println("  " + each.getIp() + "\t|  " + each.getIpCount() + " ");
                System.out.println("|-------------------|----------------|");
            });
        } catch (Exception ex) {
            System.err.println("--------------------------------------------------------");
            System.err.println("--------------------------------------------------------");
            System.err.println(ex.getMessage());
            System.err.println("--------------------------------------------------------");
            System.err.println("--------------------------------------------------------");
        }


    }

    public AccessLogService getAccessLogService() {
        return accessLogService;
    }

    @Autowired
    public void setAccessLogService(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }


}
