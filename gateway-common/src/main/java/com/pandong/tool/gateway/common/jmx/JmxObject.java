package com.pandong.tool.gateway.common.jmx;

import com.google.common.collect.Maps;

import java.util.Map;

public class JmxObject {

  private Map<String, String> data = Maps.newConcurrentMap();

  public void add(String attributeName, String value){
    data.put(attributeName, value);
  }

  public String get(String attributeName){
    return data.get(attributeName);
  }
}
