package com.pandong.tool.gateway.common;


import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author pandong
 */
@Data
public class Gateway {
  private Node gateway;
  private List<Service> services = Lists.newArrayList();

  public void addService(Service service) {
    services.add(service);
  }
}
