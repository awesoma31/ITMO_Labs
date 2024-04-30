package org.awesoma.common.util;

import org.apache.commons.cli.*;
import org.awesoma.common.Environment;

import java.io.File;
import java.io.FileNotFoundException;

public class CLIArgumentParser {
    public static void parseArgs(String[] args)  {
        Options options = new Options();

        Option port = new Option("p", true, "change working port");
        Option dbConfigFile = new Option("dbc", true, "change db configuration file");

        options.addOption(port);
        options.addOption(dbConfigFile);


        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = commandLineParser.parse(options, args);

            if (cmd.hasOption(port)) {
                Environment.setPORT(Integer.parseInt(cmd.getParsedOptionValue(port)));
            }
            if (cmd.hasOption(dbConfigFile)) {
                String path = cmd.getParsedOptionValue(dbConfigFile);
                File file = new File(path);
                if (file.exists()) {
                    Environment.setDbConfigFilePath(file.getPath());
                } else {
                    System.err.println("DB configuration file not found");
                }
            }
        } catch (ParseException e) {
            System.err.println(e);
        }

    }
}
