package com.pandong.tool.gateway.common;

import lombok.Data;

/**
 * @author pandong
 */

@Data
public class Node {
  private long id;
  private String host;
  private int port;
  private String serviceName;
}
