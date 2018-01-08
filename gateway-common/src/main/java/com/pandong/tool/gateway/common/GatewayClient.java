package com.pandong.tool.gateway.common;

import lombok.Data;

/**
 * @author pandong
 */
@Data
public class GatewayClient {
  private Node service;
  private Node gateway;
}
