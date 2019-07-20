package com.ef.model;

import com.ef.model.enumeration.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.cli.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
public class Command {


    public Command(String... args) throws ParseException {

        Options options = new Options();

        Option accesslog = Option.builder()
                .longOpt("accesslog")
                .argName("accesslog")
                .hasArg()
                .desc("accesslog ile path")
                .build();

        Option startDate = Option.builder()
                .longOpt("startDate")
                .argName("startDate")
                .hasArg()
                .desc("start Date")
                .required()
                .build();

        Option duration = Option.builder()
                .longOpt("duration")
                .argName("duration")
                .hasArg()
                .desc("duration")
                .required()
                .build();

        Option threshold = Option.builder()
                .longOpt("threshold")
                .argName("threshold")
                .hasArg()
                .desc("threshold")
                .required()
                .build();

        options.addOption(accesslog);
        options.addOption(startDate);
        options.addOption(duration);
        options.addOption(threshold);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (!cmd.hasOption("startDate") || !cmd.hasOption("duration") || !cmd.hasOption("threshold")) {
            throw new RuntimeException("args Should be either like this format\n" +
                    "--accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100\n" +
                    " or \n" +
                    "--startDate=2017-01-01.15:00:00 --duration=hourly --threshold=200");
        }


        if (cmd.hasOption("accesslog")) {
            setAccessLogPath(cmd.getOptionValue("accesslog"));
        }

        String dateTimePattern = "yyyy-dd-MM.HH:mm:ss";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

        setStartDate(LocalDateTime.parse(cmd.getOptionValue("startDate"), dateFormatter));

        String durationValue = cmd.getOptionValue("duration");
        if ("hourly".equalsIgnoreCase(durationValue)) {
            setDuration(Duration.HOURLY);
        } else if ("daily".equalsIgnoreCase(durationValue)) {
            setDuration(Duration.DAILY);
        } else {
            throw new RuntimeException("Wrong duration value allowed values: hourly, daily");
        }
        setThreshold(Integer.valueOf(cmd.getOptionValue("threshold")));


       /* for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("--")) {
                throw new RuntimeException("arg shoud start with --");
            }

            arg = arg.replace("--", "");

            if (!arg.startsWith("accesslog") || !arg.startsWith("startDate") || !arg.startsWith("duration") || !arg.startsWith("threshold")) {
                throw new RuntimeException("allowed arg values are : accesslog, startDate, duration and threshold");
            }

            if (!arg.contains("=")) {
                throw new RuntimeException("arg should be key value pair");
            }
            String[] com = arg.split("=");
            String commnad = com[0];
            String value = com[1];

            switch (commnad) {
                case "accesslog":
                    setAccessLogPath("");
                    break;
            }

        }*/
    }


    private String accessLogPath;
    private LocalDateTime startDate;
    private Duration duration;
    private Integer threshold;


}