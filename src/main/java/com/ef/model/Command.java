package com.ef.model;

import com.ef.model.enumeration.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.cli.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
public class Command {

    public static final String ACCESS_LOG_ARG = "accesslog";
    public static final String START_DATE_ARG = "startDate";
    public static final String DURATION_ARG = "duration";
    public static final String THRESHOLD_ARG = "threshold";
    public static final String ARG_DATE_FORMAT = "yyyy-MM-dd.HH:mm:ss";


    public Command(String... args) throws ParseException {

        Options options = new Options();

        Option accesslogArg = Option.builder()
                .longOpt(ACCESS_LOG_ARG)
                .argName(ACCESS_LOG_ARG)
                .hasArg()
                .desc("accesslog ile path")
                .build();

        Option startDateArg = Option.builder()
                .longOpt(START_DATE_ARG)
                .argName(START_DATE_ARG)
                .hasArg()
                .desc("start Date")
                .required()
                .build();

        Option durationArg = Option.builder()
                .longOpt(DURATION_ARG)
                .argName(DURATION_ARG)
                .hasArg()
                .desc("duration")
                .required()
                .build();

        Option thresholdArg = Option.builder()
                .longOpt(THRESHOLD_ARG)
                .argName(THRESHOLD_ARG)
                .hasArg()
                .desc("threshold")
                .required()
                .build();

        options.addOption(accesslogArg);
        options.addOption(startDateArg);
        options.addOption(durationArg);
        options.addOption(thresholdArg);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption(ACCESS_LOG_ARG)) {
            String accessLogValue = cmd.getOptionValue(ACCESS_LOG_ARG);
            setAccessLogPath(accessLogValue);
        }


        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(ARG_DATE_FORMAT);

        String startDateValue = cmd.getOptionValue(START_DATE_ARG);
        LocalDateTime startDate = LocalDateTime.parse(startDateValue, dateFormatter);
        setStartDate(startDate);

        String durationValue = cmd.getOptionValue(DURATION_ARG);
        if (Duration.HOURLY.toString().equalsIgnoreCase(durationValue)) {
            setDuration(Duration.HOURLY);
        } else if (Duration.DAILY.toString().equalsIgnoreCase(durationValue)) {
            setDuration(Duration.DAILY);
        } else {
            throw new RuntimeException("Wrong duration value, allowed values: hourly, daily");
        }
        String thresholdValue = cmd.getOptionValue(THRESHOLD_ARG);
        setThreshold(Integer.valueOf(thresholdValue));

    }


    private String accessLogPath;
    private LocalDateTime startDate;
    private Duration duration;
    private Integer threshold;


    public boolean isContainInsertComand() {
        return StringUtils.hasText(accessLogPath);
    }


}
