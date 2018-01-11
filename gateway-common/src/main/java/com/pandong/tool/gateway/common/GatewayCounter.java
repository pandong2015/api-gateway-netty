package com.pandong.tool.gateway.common;

import com.pandong.common.units.Cache;
import com.pandong.tool.gateway.common.model.Node;
import com.pandong.tool.gateway.common.model.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author pandong
 */
@Slf4j
public class GatewayCounter {
  public enum StreamType {
    READ, WRITE
  }

  private static final Cache COUNTER_CACHE = new Cache("Gateway-Counter");

  public static long getServiceRequestCounter(Service service) {
    Map<String, AtomicLong> serviceRequestCounterMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_SERVICE_REQUEST_COUNTER);
    String key = String.format(CounterName.COUNTER_NAME_SERVICE_REQUEST, service.getName());
    if (!serviceRequestCounterMap.containsKey(key)) {
      serviceRequestCounterMap.put(key, new AtomicLong(0));
    }
    return serviceRequestCounterMap.get(key).getAndIncrement();
  }

  public static void streamByteCount(StreamType type, long length) {
    Map<StreamType, AtomicLong> byteCounterMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_STREAM_BYTE_COUNTER);
    if (!byteCounterMap.containsKey(type)) {
      byteCounterMap.put(type, new AtomicLong(0));
    }
    byteCounterMap.get(type).addAndGet(length);
    log.debug("streamByteCount " + type + " bytes : " + byteCounterMap.get(type).get());
  }

  public static void streamByteCount(StreamType type, Service service, long length) {
    Map<String, AtomicLong> byteCounterMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_STREAM_BYTE_COUNTER_SERVICE);
    String key = String.format(CounterName.COUNTER_NAME_STREAN_SERVICE, type.name(), service.getName());
    if (!byteCounterMap.containsKey(key)) {
      byteCounterMap.put(key, new AtomicLong(0));
    }
    byteCounterMap.get(key).addAndGet(length);
    log.debug("streamByteCount " + key + " bytes : " + byteCounterMap.get(key).get());
  }

  public static void streamByteCount(StreamType type, Service service, Node node, long length) {
    Map<String, AtomicLong> byteCounterMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_STREAM_BYTE_COUNTER_NODE);
    String key = String.format(CounterName.COUNTER_NAME_STREAN_NODE, type.name(), service.getName(), node.getHost());
    if (!byteCounterMap.containsKey(key)) {
      byteCounterMap.put(key, new AtomicLong(0));
    }
    byteCounterMap.get(key).addAndGet(length);
    log.debug("streamByteCount " + key + " bytes : " + byteCounterMap.get(key).get());
  }

  public static void streamByteCount(StreamType type, Service service, Node node, String uri, long length) {
    Map<String, AtomicLong> byteCounterMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_STREAM_BYTE_COUNTER_URI);
    String key = String.format(CounterName.COUNTER_NAME_STREAN_URI, type.name(), service.getName(), node.getHost(), uri);
    if (!byteCounterMap.containsKey(key)) {
      byteCounterMap.put(key, new AtomicLong(0));
    }
    byteCounterMap.get(key).addAndGet(length);
    log.debug("streamByteCount " + key + " bytes : " + byteCounterMap.get(key).get());
  }

  public static void urlCount(Service service, String uri) {
    Map<String, AtomicLong> serviceMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_SERVICE_COUNTER);
    Map<String, AtomicLong> urlMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_SERVICE_URL_COUNTER);
    String key = String.format(CounterName.COUNTER_NAME_SERVICE_URL, service.getName(), uri);
    if (!urlMap.containsKey(key)) {
      urlMap.put(key, new AtomicLong());
    }
    if (!serviceMap.containsKey(service.getName())) {
      serviceMap.put(service.getName(), new AtomicLong());
    }
    serviceMap.get(service.getName()).getAndIncrement();
    urlMap.get(key).getAndIncrement();
    log.debug(CounterName.CACHE_NAME_SERVICE_COUNTER + " [" + service.getName() + "] : "
            + serviceMap.get(service.getName()).get());
    log.debug(CounterName.CACHE_NAME_SERVICE_URL_COUNTER + " [" + key + "] : " + urlMap.get(key).get());
  }

  public static void urlCount(Service service, Node node, String uri) {
    urlCount(service, uri);
    Map<String, AtomicLong> nodeMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_NODE_COUNTER);
    Map<String, AtomicLong> urlMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_NODE_URL_COUNTER);
    String key = String.format(CounterName.COUNTER_NAME_NODE_URL, service.getName(), node.getHost(), uri);
    if (!urlMap.containsKey(key)) {
      urlMap.put(key, new AtomicLong());
    }
    String nodeKey = String.format(CounterName.COUNTER_NAME_SERVICE_NODE, service.getName(), node.getHost());
    if (!nodeMap.containsKey(nodeKey)) {
      nodeMap.put(nodeKey, new AtomicLong());
    }
    nodeMap.get(nodeKey).getAndIncrement();
    urlMap.get(key).getAndIncrement();
    log.debug(CounterName.CACHE_NAME_NODE_COUNTER + " [" + nodeKey + "] : " + nodeMap.get(nodeKey).get());
    log.debug(CounterName.CACHE_NAME_NODE_URL_COUNTER + " [" + key + "] : " + urlMap.get(key).get());
  }

  public static void requestByteCount(StreamType type, long requestId, long length) {
    Map<String, AtomicLong> urlMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_REQUEST_BYTE_COUNTER);
    String key = String.format(CounterName.COUNTER_NAME_STREAN_REQUEST, type.name(), requestId);
    if (!urlMap.containsKey(key)) {
      urlMap.put(key, new AtomicLong());
    }
    urlMap.get(key).getAndAdd(length);
    log.debug("streamByteCount " + key + " bytes : " + urlMap.get(key).get());
  }

  public static void cleanRequestByteCount(StreamType type, long requestId) {
    Map<Long, AtomicLong> urlMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_REQUEST_BYTE_COUNTER);
    String key = String.format(CounterName.COUNTER_NAME_STREAN_REQUEST, type.name(), requestId);
    if (urlMap.containsKey(key)) {
      urlMap.remove(key);
    }
  }

  public static long getRequestByteCount(StreamType type, long requestId) {
    Map<Long, AtomicLong> urlMap = COUNTER_CACHE.map(CounterName.CACHE_NAME_REQUEST_BYTE_COUNTER);
    String key = String.format(CounterName.COUNTER_NAME_STREAN_REQUEST, type.name(), requestId);
    if (urlMap.containsKey(key)) {
      return urlMap.get(key).get();
    }
    return 0;
  }

  public interface CounterName {
    String CACHE_NAME_REQUEST_BYTE_COUNTER = "request-byte-mapping";
    String CACHE_NAME_STREAM_BYTE_COUNTER = "stream-byte-mapping";
    String CACHE_NAME_STREAM_BYTE_COUNTER_SERVICE = "stream-byte-service-mapping";
    String CACHE_NAME_STREAM_BYTE_COUNTER_NODE = "stream-byte-node-mapping";
    String CACHE_NAME_STREAM_BYTE_COUNTER_URI = "stream-byte-uri-mapping";
    String CACHE_NAME_SERVICE_REQUEST_COUNTER = "service-request-mapping";
    String CACHE_NAME_SERVICE_URL_COUNTER = "service-url-mapping";
    String CACHE_NAME_SERVICE_COUNTER = "service-mapping";
    String CACHE_NAME_NODE_URL_COUNTER = "node-url-mapping";
    String CACHE_NAME_NODE_COUNTER = "node-mapping";

    String COUNTER_NAME_SERVICE_REQUEST = "service-request-%s";
    String COUNTER_NAME_SERVICE_URL = "service-url-%s-[%s]";
    String COUNTER_NAME_NODE_URL = "service-url-%s-[%s:%s]";
    String COUNTER_NAME_SERVICE_NODE = "service[%s]-node[%s]";

    String COUNTER_NAME_STREAN_SERVICE = "stream-[%s]-service[%s]";
    String COUNTER_NAME_STREAN_NODE = "stream-[%s]-service[%s]-node[%s]";
    String COUNTER_NAME_STREAN_URI = "stream-[%s]-service[%s]-node[%s]-%s";
    String COUNTER_NAME_STREAN_REQUEST = "stream-[%s]-%d";
  }
}
