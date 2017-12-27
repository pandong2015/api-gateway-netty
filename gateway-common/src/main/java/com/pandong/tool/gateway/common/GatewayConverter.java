package com.pandong.tool.gateway.common;

import com.pandong.common.units.StringUtils;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class GatewayConverter implements Converter {
  public static final String ROOT_NAME_GATEWAY = "gateway";
  public static final String NODE_NAME_SERVICES = "services";
  public static final String NODE_NAME_SERVICE = "service";
  public static final String NODE_NAME_NODES = "nodes";
  public static final String NODE_NAME_NODE = "node";

  public static final String ATTRIBUT_NAME_SERVERID = "serverId";
  public static final String ATTRIBUT_NAME_SERVERNAME = "serverName";
  public static final String ATTRIBUT_NAME_SERVERDOMAIN = "domain";
  public static final String ATTRIBUT_NAME_SERVERTYPE = "type";
  public static final String ATTRIBUT_NAME_PROXYPORT = "proxyPort";
  public static final String ATTRIBUT_NAME_IP = "host";
  public static final String ATTRIBUT_NAME_PORT = "port";
  public static final String ATTRIBUT_NAME_HEATHCHECKURL = "heathCheckUrl";

  @Override
  public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
    Gateway gateway = (Gateway) o;
    if (gateway == null) {
      return;
    }
    writer.startNode(NODE_NAME_SERVICES);
    if (gateway.getServices() != null && !gateway.getServices().isEmpty()) {
      gateway.getServices().forEach(service -> {
        writer.startNode(NODE_NAME_SERVICE);
        writer.addAttribute(ATTRIBUT_NAME_SERVERID, String.valueOf(service.getServiceId()));
        writer.addAttribute(ATTRIBUT_NAME_SERVERNAME, service.getServiceName());
        writer.addAttribute(ATTRIBUT_NAME_SERVERTYPE, service.getType().name());
        writer.addAttribute(ATTRIBUT_NAME_PROXYPORT, String.valueOf(service.getProxyPort()));
        writer.addAttribute(ATTRIBUT_NAME_HEATHCHECKURL, service.getHeathCheckUrl());
        writer.addAttribute(ATTRIBUT_NAME_SERVERDOMAIN, service.getDomain());
        writer.startNode(NODE_NAME_NODES);
        if (service.getNodes() != null && !service.getNodes().isEmpty()) {
          service.getNodes().forEach(node -> {
            writer.startNode(NODE_NAME_NODE);
            writer.addAttribute(ATTRIBUT_NAME_IP, node.getHost());
            writer.addAttribute(ATTRIBUT_NAME_PORT, String.valueOf(node.getPort()));
            writer.endNode();
          });
        }
        writer.endNode();
        writer.endNode();
      });
    }
    writer.endNode();
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    Gateway gateway = new Gateway();
    reader.moveDown();
    if (reader.getNodeName().equalsIgnoreCase(NODE_NAME_SERVICES)) {
      while (reader.hasMoreChildren()) {
        reader.moveDown();
        Service service = new Service();
        if (reader.getNodeName().equalsIgnoreCase(NODE_NAME_SERVICE)) {
          if (!StringUtils.isNull(reader.getAttribute(ATTRIBUT_NAME_SERVERID))) {
            service.setServiceId(Integer.valueOf(reader.getAttribute(ATTRIBUT_NAME_SERVERID)));
          }
          if (!StringUtils.isNull(reader.getAttribute(ATTRIBUT_NAME_PROXYPORT))) {
            service.setProxyPort(Integer.parseInt(reader.getAttribute(ATTRIBUT_NAME_PROXYPORT)));
          }
          if (!StringUtils.isNull(reader.getAttribute(ATTRIBUT_NAME_SERVERTYPE))) {
            service.setType(Service.ServiceType.valueOf(reader.getAttribute(ATTRIBUT_NAME_SERVERTYPE)));
          } else {
            service.setType(Service.ServiceType.HTTP);
          }
          service.setHeathCheckUrl(reader.getAttribute(ATTRIBUT_NAME_HEATHCHECKURL));
          service.setServiceName(reader.getAttribute(ATTRIBUT_NAME_SERVERNAME));
          service.setDomain(reader.getAttribute(ATTRIBUT_NAME_SERVERDOMAIN));
          reader.moveDown();
          if (reader.getNodeName().equalsIgnoreCase(NODE_NAME_NODES)) {
            while (reader.hasMoreChildren()) {
              reader.moveDown();
              Node node = new Node();
              if (reader.getNodeName().equalsIgnoreCase(NODE_NAME_NODE)) {
                node.setHost(reader.getAttribute(ATTRIBUT_NAME_IP));
                if (!StringUtils.isNull(reader.getAttribute(ATTRIBUT_NAME_PORT))) {
                  node.setPort(Integer.valueOf(reader.getAttribute(ATTRIBUT_NAME_PORT)));
                }
              }
              service.addNode(node);
              reader.moveUp();
            }
          }
          reader.moveUp();
        }
        gateway.addNodeGroup(service);
        reader.moveUp();
      }
    }
    reader.moveUp();
    return gateway;
  }

  @Override
  public boolean canConvert(Class aClass) {
    return aClass.equals(Gateway.class);
  }
}
