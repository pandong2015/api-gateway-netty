package com.pandong.tool.gateway.common;

import com.pandong.common.units.Cache;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class GatewayCounter {
  public enum StreamType {
    READ, WRITE
  }

  private static final Cache COUNTER_CACHE = new Cache("Gateway-Counter");

  public static long getServiceRequestCounter(Service service) {
    Map<String, AtomicLong> serviceRequestCounterMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_SERVICE_REQUEST_COUNTER);
    String key = String.format(CounterName.COUNTER_NAME_SERVICE_REQUEST, service.getServiceName());
    if (!serviceRequestCounterMap.containsKey(key)) {
      serviceRequestCounterMap.put(key, new AtomicLong(0));
    }
    return serviceRequestCounterMap.get(key).getAndIncrement();
  }

  public static void streamByteCount(StreamType type, int length) {
    Map<StreamType, AtomicLong> byteCounterMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_STREAM_BYTE_COUNTER);
    if (!byteCounterMap.containsKey(type)) {
      byteCounterMap.put(type, new AtomicLong(0));
    }
    byteCounterMap.get(type).addAndGet(length);
  }

  public static void urlCount(Service service, String uri) {
    Map<String, AtomicLong> urlMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_SERVICE_URL_COUNTER);
    String key = String.format(CounterName.COUNTER_NAME_SERVICE_URL, service.getServiceName(), uri);
    if (!urlMap.containsKey(key)) {
      urlMap.put(key, new AtomicLong());
    }
    urlMap.get(key).getAndIncrement();
  }

  public interface CounterName {
    String CACHE_NAME_STREAM_BYTE_COUNTER = "stream-byte-mapping";
    String CACHE_NAME_SERVICE_REQUEST_COUNTER = "service-request-mapping";
    String CACHE_NAME_SERVICE_URL_COUNTER = "service-url-mapping";

    String COUNTER_NAME_SERVICE_REQUEST = "service-request-%s";
    String COUNTER_NAME_SERVICE_URL = "service-url-%s-[%s]";
  }
}
