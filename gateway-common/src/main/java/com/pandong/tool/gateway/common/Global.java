package com.pandong.tool.gateway.common;

import com.pandong.common.units.Cache;
import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.pandong.tool.gateway.common.GatewayConverter.ROOT_NAME_GATEWAY;

public class Global {
  private static final XStream xstream = new XStream();
  public static final String USER_HOME = System.getProperty("user.home");
  public static final String WORK_SPACE = USER_HOME + File.separator + ".gateway";
  public static final String CONFIG_NAME = WORK_SPACE + File.separator + "gateway.xml";

  public static final Cache GATEWAY_CACHE = new Cache("Gateway");

  static {
    xstream.autodetectAnnotations(true);
    xstream.alias(ROOT_NAME_GATEWAY, Gateway.class);
    xstream.registerConverter(new GatewayConverter());
  }

  public static Gateway readConfig() throws FileNotFoundException {
    return readConfig(CONFIG_NAME);
  }

  public static Gateway readConfig(String configFile) throws FileNotFoundException {
    return (Gateway) xstream.fromXML(new BufferedInputStream(new FileInputStream(configFile)), new Gateway());
  }

  public static void writeConfig(Gateway gateway) throws IOException {
    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(CONFIG_NAME))) {
      xstream.toXML(gateway, out);
    }
  }

}
