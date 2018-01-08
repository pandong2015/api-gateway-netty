package com.pandong.tool.gateway.server;

import com.pandong.tool.gateway.common.Gateway;
import com.pandong.tool.gateway.common.Global;
import com.pandong.tool.gateway.server.utils.ServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * @author pandong
 */

@Slf4j
public class Startup {

  public static void main(String[] args) {
    CommandLine commandLine = null;
    try {
      commandLine = uploadCommandLine(args);
    } catch (ParseException e) {
      log.error("parse command error! " + e.getMessage(), e);
      System.exit(1);
    }
    Gateway gateway = null;
    String configPath = null;
    try {

      if (commandLine.hasOption("f")) {
        configPath = commandLine.getOptionValue("f");
        gateway = Global.readConfig();
      } else {
        configPath = Global.CONFIG_SERVER_NAME;
        gateway = Global.readConfig();
      }
      log.info("load config file [" + configPath + "] success.");
    } catch (IOException e) {
      log.error("load config file[" + configPath + "] fail. " + e.getMessage(), e);
      System.exit(1);
    }
    ServerUtil.parseConfig(gateway);
    log.info("parse config success.");
    ServerUtil.startAllProxyServers();
  }

  public static CommandLine uploadCommandLine(String[] args) throws ParseException {
    CommandLineParser parser = new BasicParser();
    Options options = new Options();
    options.addOption("f", "file", true, "config file.");
    return parser.parse(options, args);
  }



}
