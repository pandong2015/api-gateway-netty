package com.pandong.tool.gateway.common;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class Service {
  public enum ServiceType{
    HTTP
  }
  private long serviceId;
  private String serviceName;
  private String domain;
  private int proxyPort;
  private String heathCheckUrl;
  private ServiceType type = ServiceType.HTTP;
  private List<Node> nodes = Lists.newArrayList();

  public void addNode(Node node){
    nodes.add(node);
  }
}
