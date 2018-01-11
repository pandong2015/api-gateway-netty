package com.pandong.tool.gateway.common.test;

import com.pandong.tool.gateway.common.model.Gateway;
import com.pandong.tool.gateway.common.Global;
import com.pandong.tool.gateway.common.model.Node;
import com.pandong.tool.gateway.common.model.Service;

import java.io.IOException;

public class TestXStream {
  public static void main(String[] args) throws IOException {
    Gateway gateway = new Gateway();
    Service nodeGroup = new Service();
    nodeGroup.setId(1);
    nodeGroup.setName("test");
    nodeGroup.setHeathCheckUrl("/heathCheck");
    Node node = new Node();
    node.setHost("192.168.1.1");
    node.setPort(1111);
    nodeGroup.addNode(node);
    gateway.addService(nodeGroup);
    Global.writeConfig(gateway);
    Gateway gateway1 = Global.readConfig();
    System.out.println(gateway1);
  }
}
