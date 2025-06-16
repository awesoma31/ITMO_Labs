package org.awesoma.common.util;

import org.apache.commons.cli.*;
import org.awesoma.common.Environment;

import java.io.File;

/**
 * Class responsible for application flag parsing on start
 */
public class CLIArgumentParser {
    public static void parseArgs(String[] args) {
        Options options = new Options();

        Option port = new Option("p", true, "set working port");
        Option dbConfigFile = new Option("dbc", true, "set db configuration file");
        Option help = new Option("help", false, "prints this message");
        Option host = new Option("h", "host", true, "set host");

        options.addOption(port);
        options.addOption(dbConfigFile);
        options.addOption(help);
        options.addOption(host);


        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = commandLineParser.parse(options, args);

            if (cmd.hasOption(help)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("lab8 flags", options);
                System.exit(0);
            }
            if (cmd.hasOption(host)) {
                Environment.setHOST(cmd.getParsedOptionValue(host));
            }
            if (cmd.hasOption(port)) {
                Environment.setPORT(cmd.getParsedOptionValue(port));
            }
            if (cmd.hasOption(dbConfigFile)) {
                setConfigFile(cmd, dbConfigFile);
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    private static void setConfigFile(CommandLine cmd, Option dbConfigFile) throws ParseException {
        String path = cmd.getParsedOptionValue(dbConfigFile);
        File file = new File(path);
        if (file.exists()) {
            Environment.setDbConfigFilePath(file.getPath());
        } else {
            System.err.println("DB configuration file not found");
        }
    }
}
