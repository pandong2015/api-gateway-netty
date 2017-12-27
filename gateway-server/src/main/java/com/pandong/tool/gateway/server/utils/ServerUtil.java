package com.pandong.tool.gateway.server.utils;

import com.pandong.tool.gateway.common.Gateway;
import com.pandong.tool.gateway.common.Global;
import com.pandong.tool.gateway.common.Node;
import com.pandong.tool.gateway.common.Service;
import com.pandong.tool.gateway.server.handlers.MetricsHandler;
import com.pandong.tool.gateway.server.handlers.ProxyChannelHandler;
import com.pandong.tool.gateway.server.handlers.ProxyResponseChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
public class ServerUtil {
  private static EventLoopGroup masterGroup = new NioEventLoopGroup();
  private static EventLoopGroup workerGroup = new NioEventLoopGroup();

  public static void parseConfig(Gateway gateway) {
    Global.GATEWAY_CACHE.save(CacheName.CACHE_NAME_GATEWAY_CONFIG, gateway);
    Map<String, Service> serviceMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_MAP);
    Map<Integer, Service> servicePortMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_PORT_MAP);
    Map<String, Node> serviceNodeMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_NODE_MAP);
    gateway.getServices().forEach(service -> {
      serviceMap.put(service.getServiceName(), service);
      servicePortMap.put(service.getProxyPort(), service);
      List<Node> nodeList = Global.GATEWAY_CACHE.list(CacheName.CACHE_NAME_SERVICE_NODE_LIST + service.getServiceName());
      service.getNodes().forEach(node -> {
        serviceNodeMap.put(getNodeKey(service, node), node);
        nodeList.add(node);
      });
      nodeList.sort(new Comparator<Node>() {
        @Override
        public int compare(Node node, Node t1) {
          int ipresult = node.getHost().compareTo(t1.getHost());
          if (ipresult == 0) {
            return ((Integer) node.getPort()).compareTo(t1.getPort());
          }
          return ipresult;
        }
      });
    });
  }

  public static void cacheKeepAliveChannel(Service service, Node node, Channel channel){
    Map<String, Channel> servicePortMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_PORT_MAP);
  }

  public static Service getService(int port) {
    Map<Integer, Service> servicePortMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_PORT_MAP);
    return servicePortMap.get(port);
  }

  public static String getNodeKey(Service service, Node node) {
    return service.getServiceName() + "[" + node.getHost() + ":" + node.getPort() + "]";
  }

  public interface CacheName {
    String CACHE_NAME_GATEWAY_CONFIG = "gatewat-config";
    String CACHE_NAME_SERVICE_MAP = "service-mapping";
    String CACHE_NAME_SERVICE_PORT_MAP = "service-port-mapping";
    String CACHE_NAME_SERVICE_NODE_MAP = "service-node-mapping";
    String CACHE_NAME_SERVICE_NODE_LIST = "service-node-list-";
    String CACHE_NAME_BOOTSTRAP_MAP = "bootstrap-mapping";

    String CACHE_NAME_BOOTSTRAP_TYPE_HTTP = "http";
  }

  public static void startAllProxyServers() {
    Map<String, Service> serviceMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_MAP);
    serviceMap.entrySet().forEach(entry -> startProxyServer(entry.getValue()));
  }

  public static void startProxyServer(Service service) {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(masterGroup, workerGroup).channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {

              @Override
              public void initChannel(SocketChannel ch) throws Exception {
                CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
//                ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                ch.pipeline().addLast(new MetricsHandler());
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(65536));
                ch.pipeline().addLast(new ChunkedWriteHandler());
                ch.pipeline().addLast(new CorsHandler(corsConfig));
                ch.pipeline().addLast(new ProxyChannelHandler());
              }
            });
    try {
      bootstrap.bind(service.getProxyPort()).get();
      log.info("start proxy server[" + service.getServiceName() + ":" + service.getProxyPort() + "] success!!!");
    } catch (Exception e) {
      log.error("start proxy server[" + service.getServiceName() + ":" + service.getProxyPort() + "] fail, "
              + e.getMessage(), e);
    }

  }

  public static Bootstrap getBootstrap(Service service) {
    Map<String, Bootstrap> nodeBootstrapMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_BOOTSTRAP_MAP);
    Bootstrap bootstrap = null;
    switch (service.getType()) {
      case HTTP:
        if (!nodeBootstrapMap.containsKey(CacheName.CACHE_NAME_BOOTSTRAP_TYPE_HTTP)) {
          bootstrap = initHttpClientBootstrap();
          nodeBootstrapMap.put(CacheName.CACHE_NAME_BOOTSTRAP_TYPE_HTTP, bootstrap);
        }else{
          bootstrap = nodeBootstrapMap.get(CacheName.CACHE_NAME_BOOTSTRAP_TYPE_HTTP);
        }
        break;
    }
    return bootstrap;
  }

  public static Bootstrap initHttpClientBootstrap() {
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
//        p.addLast(new LoggingHandler(LogLevel.INFO));
        p.addLast(new HttpClientCodec());
        p.addLast(new HttpContentDecompressor());
        p.addLast(new ProxyResponseChannelHandler());
      }
    });

    return bootstrap;
  }

  public static Node loadBalancer(long requestTimes, List<Node> nodeList) {
    long index = requestTimes % nodeList.size();
    return nodeList.get((int) index);
  }

  public interface ChannelAttribute{
    AttributeKey<Channel> PROXY_CHANNEL = AttributeKey.newInstance("proxy_channel");
    AttributeKey<Channel> REQUEST_CHANNEL = AttributeKey.newInstance("request_channel");

    AttributeKey<Long> REQUEST_TIME_START = AttributeKey.newInstance("request_time_start");
  }
}
