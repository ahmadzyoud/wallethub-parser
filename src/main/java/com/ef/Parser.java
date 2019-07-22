package com.ef;

import com.ef.domain.AccessIpStatistics;
import com.ef.domain.BlockedIp;
import com.ef.model.Command;
import com.ef.repository.BlockedIpRepository;
import com.ef.repository.CustomRepositoryImpl;
import com.ef.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
public class Parser implements CommandLineRunner {

    private AccessLogService accessLogService;

    private BlockedIpRepository blockedIpRepository;


    public static void main(String[] args) {
        SpringApplication.run(Parser.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Command command = new Command(args);

        if (command.isContainInsertComand()) {
            accessLogService.insert(command);
        }

        List<AccessIpStatistics> result = accessLogService.search(command);
        System.out.println("---------------------------------------");
        System.out.println("|--------IP---------|------COUNT------|");
        result.forEach(each -> {
            System.out.println("  " + each.getIp() + "\t|  " + each.getIpCount() + " ");
            BlockedIp blockedIp = new BlockedIp();
            blockedIp.setIp(each.getIp());
            blockedIp.setAccessCount(each.getIpCount());
            blockedIp.setBlockReason("The IP accessed more than " + command.getThreshold() + " " + command.getDuration().toString());
            blockedIpRepository.save(blockedIp);
            System.out.println("|-------------------|----------------|");

        });



    }

    public AccessLogService getAccessLogService() {
        return accessLogService;
    }

    @Autowired
    public void setAccessLogService(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    public BlockedIpRepository getBlockedIpRepository() {
        return blockedIpRepository;
    }

    @Autowired
    public void setBlockedIpRepository(BlockedIpRepository blockedIpRepository) {
        this.blockedIpRepository = blockedIpRepository;
    }
}
