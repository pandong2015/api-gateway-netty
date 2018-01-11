package com.pandong.tool.gateway.common.model;

import lombok.Data;

/**
 * @author pandong
 */
@Data
public class GatewayClient {
  private Node service;
  private Node gateway;
}
