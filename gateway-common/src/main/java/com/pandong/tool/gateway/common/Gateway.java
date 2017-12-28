package com.pandong.tool.gateway.common;


import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author pandong
 */
@Data
public class Gateway {
  private List<Service> services = Lists.newArrayList();

  public void addNodeGroup(Service service) {
    services.add(service);
  }
}
