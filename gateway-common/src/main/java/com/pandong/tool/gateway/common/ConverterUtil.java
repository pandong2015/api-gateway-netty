package com.pandong.tool.gateway.common;

import com.pandong.common.units.StringUtils;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author pandong
 */
public class ConverterUtil {
  public static void marshalService(HierarchicalStreamWriter writer, Service service) {
    writer.startNode(GatewayConverterName.NODE_NAME_SERVICE);
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERID, String.valueOf(service.getId()));
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERNAME, service.getName());
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERTYPE, service.getType().name());
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_PROXYPORT, String.valueOf(service.getProxyPort()));
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_HEATHCHECKURL, service.getHeathCheckUrl());
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERDOMAIN, service.getDomain());
    writer.startNode(GatewayConverterName.NODE_NAME_NODES);
    if (service.getNodes() != null && !service.getNodes().isEmpty()) {
      service.getNodes().forEach(node -> marshalNode(writer, node));
    }
    writer.endNode();
    writer.endNode();
  }

  public static void marshalNode(HierarchicalStreamWriter writer, Node node) {
    writer.startNode(GatewayConverterName.NODE_NAME_NODE);
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_NODEHOST, node.getHost());
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_NODEPORT, String.valueOf(node.getPort()));
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_NODEID, String.valueOf(node.getId()));
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_NODESERVICENAME, node.getServiceName());
    writer.endNode();
  }

  public static Service unmarshalService(HierarchicalStreamReader reader) {
    Service service = new Service();
    if (reader.getNodeName().equalsIgnoreCase(GatewayConverterName.NODE_NAME_SERVICE)) {
      if (!StringUtils.isNull(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERID))) {
        service.setId(Integer.valueOf(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERID)));
      }
      if (!StringUtils.isNull(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_PROXYPORT))) {
        service.setProxyPort(Integer.parseInt(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_PROXYPORT)));
      }
      if (!StringUtils.isNull(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERTYPE))) {
        service.setType(Service.ServiceType.valueOf(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERTYPE)));
      } else {
        service.setType(Service.ServiceType.HTTP);
      }
      service.setHeathCheckUrl(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_HEATHCHECKURL));
      service.setName(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERNAME));
      service.setDomain(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_SERVERDOMAIN));
      while (reader.hasMoreChildren()) {
        reader.moveDown();
        if (reader.getNodeName().equalsIgnoreCase(GatewayConverterName.NODE_NAME_NODES)) {
          while (reader.hasMoreChildren()) {
            reader.moveDown();
            service.addNode(unmarshalNode(reader));
            reader.moveUp();
          }
        }
        reader.moveUp();
      }

    }
    return service;
  }

  public static Node unmarshalNode(HierarchicalStreamReader reader) {
    Node node = new Node();
    if (reader.getNodeName().equalsIgnoreCase(GatewayConverterName.NODE_NAME_NODE)) {
      node.setHost(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_NODEHOST));
      node.setServiceName(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_NODESERVICENAME));
      if (!StringUtils.isNull(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_NODEPORT))) {
        node.setPort(Integer.valueOf(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_NODEPORT)));
      }
      if (!StringUtils.isNull(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_NODEID))) {
        node.setId(Long.parseLong(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_NODEID)));
      }
    }
    return node;
  }
}
