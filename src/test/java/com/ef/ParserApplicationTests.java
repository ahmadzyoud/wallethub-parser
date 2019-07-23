package com.ef;

import com.ef.domain.AccessIpStatistics;
import com.ef.model.Command;
import com.ef.repository.AccessLogRepository;
import com.ef.repository.BlockedIpRepository;
import com.ef.service.AccessLogService;
import org.apache.commons.cli.ParseException;
import org.hamcrest.core.IsSame;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest

public class ParserApplicationTests {

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Autowired
    private BlockedIpRepository blockedIpRepository;

    @Autowired
    private AccessLogService accessLogService;


    @Test
    @Order(1)
    public void testInsert() throws ParseException {

        accessLogRepository.deleteAll();


        String[] args = {"--accesslog=G:/Java_MySQL_Test/access_lite.log", "--startDate=2017-01-01.15:00:00",
                "--duration=hourly", "--threshold=200"};
        Command command = new Command(args);
        if (command.isContainInsertComand()) {
            accessLogService.insert(command);
        }

        Long count = accessLogRepository.count();

        assertThat(count, IsSame.sameInstance(5L));

    }

    @Test
    @Order(2)
    public void testSearch() throws ParseException {
        blockedIpRepository.deleteAll();
        String[] args = {"--startDate=2017-01-01.00:00:00",
                "--duration=hourly", "--threshold=3"};
        Command command = new Command(args);
        List<AccessIpStatistics> result = accessLogService.search(command);

        assertThat(result.size(), IsSame.sameInstance(1));

        assertEquals("Blocked IP", "192.168.234.82", result.get(0).getIp());
    }

}
