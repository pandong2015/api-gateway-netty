package com.pandong.tool.gateway.common.jmx;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class JmxObject {
  private String name;
  private Map<String, String> data = Maps.newConcurrentMap();

  public void add(String attributeName, String value) {
    data.put(attributeName, value);
  }

  public String get(String attributeName) {
    return data.get(attributeName);
  }

  public Set<String> getAttributeNames() {
    return data.keySet();
  }

  @Override
  public String toString() {
    return "JmxObject{" +
            "name='" + name + '\'' +
            ", data=" + data +
            '}';
  }
}
