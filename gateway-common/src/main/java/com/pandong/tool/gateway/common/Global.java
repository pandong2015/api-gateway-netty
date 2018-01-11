package com.pandong.tool.gateway.common;

import com.google.common.collect.Maps;
import com.pandong.common.units.Cache;
import com.pandong.tool.gateway.common.model.Gateway;
import com.pandong.tool.gateway.common.model.GatewayClient;
import com.pandong.tool.gateway.common.model.Heartbeat;
import com.pandong.tool.gateway.common.model.converter.GatewayClientConverter;
import com.pandong.tool.gateway.common.model.converter.GatewayConverter;
import com.pandong.tool.gateway.common.model.converter.HeartbeatConverter;
import com.pandong.tool.gateway.common.model.converter.OperateResponseConverter;
import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

import static com.pandong.tool.gateway.common.model.converter.GatewayConverterName.*;

/**
 * @author pandong
 */
public class Global {
  public static final Charset DEFULT_CHARSET = Charset.forName("UTF-8");
  private static final XStream xstream = new XStream();
  public static final String USER_HOME = System.getProperty("user.home");
  public static final String WORK_SPACE = USER_HOME + File.separator + ".gateway";
  public static final String CONFIG_SERVER_NAME = WORK_SPACE + File.separator + "gateway.xml";
  public static final String CONFIG_CLIENT_NAME = WORK_SPACE + File.separator + "client.xml";

  public static final Map<String, String[]> JMX_OBJECT_ATTRIBUTE_MAP = Maps.newHashMap();

  public static final Cache GATEWAY_CACHE = new Cache("Gateway");

  static {
    xstream.autodetectAnnotations(true);
    xstream.alias(NODE_NAME_GATEWAY, Gateway.class);
    xstream.alias(NODE_NAME_CLIENT, GatewayClient.class);
    xstream.alias(NODE_NAME_ROOT, Heartbeat.class);
    xstream.registerConverter(new GatewayConverter());
    xstream.registerConverter(new GatewayClientConverter());
    xstream.registerConverter(new OperateResponseConverter());
    xstream.registerConverter(new HeartbeatConverter());

    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_CLASS_LOADING, Heartbeat.ATTRIBUTE_NAMES_CLASS_LOADING);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_CODE_CACHE_MANAGER, Heartbeat.ATTRIBUTE_NAMES_CODE_CACHE_MANAGER);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_MEMORY, Heartbeat.ATTRIBUTE_NAMES_MEMORY);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_OPERATING_SYSTEM, Heartbeat.ATTRIBUTE_NAMES_OPERATING_SYSTEM);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_PS_EDEN_SPACE, Heartbeat.ATTRIBUTE_NAMES_PS_EDEN_SPACE);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_PS_MARK_SWEEP, Heartbeat.ATTRIBUTE_NAMES_PS_MARK_SWEEP);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_PS_OLD_GEN, Heartbeat.ATTRIBUTE_NAMES_PS_OLD_GEN);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_PS_SCAVENGE, Heartbeat.ATTRIBUTE_NAMES_PS_SCAVENGE);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_PS_SURVIVOR_SPACE, Heartbeat.ATTRIBUTE_NAMES_PS_SURVIVOR_SPACE);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_RUNTIME, Heartbeat.ATTRIBUTE_NAMES_RUNTIME);
    JMX_OBJECT_ATTRIBUTE_MAP.put(Heartbeat.OBJECT_NAME_THREADING, Heartbeat.ATTRIBUTE_NAMES_THREADING);
  }

  public static Gateway readConfig() throws FileNotFoundException {
    return readConfig(CONFIG_SERVER_NAME);
  }

  public static Gateway readConfig(String configFile) throws FileNotFoundException {
    return (Gateway) xstream.fromXML(new BufferedInputStream(new FileInputStream(configFile)), new Gateway());
  }

  public static GatewayClient readClientConfig() throws FileNotFoundException {
    return readClientConfig(CONFIG_CLIENT_NAME);
  }

  public static GatewayClient readClientConfig(String configFile) throws FileNotFoundException {
    return (GatewayClient) xstream.fromXML(new BufferedInputStream(new FileInputStream(configFile)), new GatewayClient());
  }

  public static GatewayClient paresClientConfig(String config) {
    return string2Obj(config, new GatewayClient());
  }

  public static void writeConfig(Gateway gateway) throws IOException {
    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(CONFIG_SERVER_NAME))) {
      xstream.toXML(gateway, out);
    }
  }

  public static <T> String object2Xml(T t) {
    return xstream.toXML(t);
  }

  public static <T> T string2Obj(String xml, T t){
    return (T)xstream.fromXML(xml, t);
  }

}
