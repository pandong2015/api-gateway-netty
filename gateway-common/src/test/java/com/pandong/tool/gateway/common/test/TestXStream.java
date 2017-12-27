package com.pandong.tool.gateway.common.test;

import com.pandong.tool.gateway.common.Gateway;
import com.pandong.tool.gateway.common.Global;
import com.pandong.tool.gateway.common.Node;
import com.pandong.tool.gateway.common.Service;

import java.io.IOException;

public class TestXStream {
  public static void main(String[] args) throws IOException {
    Gateway gateway = new Gateway();
    Service nodeGroup = new Service();
    nodeGroup.setServiceId(1);
    nodeGroup.setServiceName("test");
    nodeGroup.setHeathCheckUrl("/heathCheck");
    Node node = new Node();
    node.setHost("192.168.1.1");
    node.setPort(1111);
    nodeGroup.addNode(node);
    gateway.addNodeGroup(nodeGroup);
    Global.writeConfig(gateway);
    Gateway gateway1 = Global.readConfig();
    System.out.println(gateway1);
  }
}
