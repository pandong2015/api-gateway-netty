package com.pandong.tool.gateway.server.utils;

import com.pandong.tool.gateway.common.model.Gateway;
import com.pandong.tool.gateway.common.Global;
import com.pandong.tool.gateway.common.model.Node;
import com.pandong.tool.gateway.common.model.Service;
import com.pandong.tool.gateway.common.exceptions.GatewayException;
import com.pandong.tool.gateway.common.exceptions.ServiceNodeExistException;
import com.pandong.tool.gateway.common.exceptions.ServicePortBindException;
import com.pandong.tool.gateway.common.model.GatewayProto;
import com.pandong.tool.gateway.server.handlers.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author pandong
 */

@Slf4j
public class ServerUtil {
  private static EventLoopGroup masterGroup = new NioEventLoopGroup();
  private static EventLoopGroup workerGroup = new NioEventLoopGroup();

  public static void parseConfig(Gateway gateway) {
    Global.GATEWAY_CACHE.save(CacheName.CACHE_NAME_GATEWAY_CONFIG, gateway);
    gateway.getServices().forEach(service -> cacheService(service));
  }

  public static void cacheService(Service service) {
    Map<String, Service> serviceMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_MAP);
    serviceMap.put(service.getName(), service);
    service.getNodes().forEach(node -> refreshServiceNode(service, node));
  }

  public static void refreshServiceNode(Service service, Node node) {
    Map<String, Node> serviceNodeMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_NODE_MAP);
    List<Node> nodeList = Global.GATEWAY_CACHE.list(CacheName.CACHE_NAME_SERVICE_NODE_LIST + service.getName());
    String key = getNodeKey(service, node);
    if (!serviceNodeMap.containsKey(key)) {
      serviceNodeMap.put(key, node);
      nodeList.add(node);
    } else {
      throw new ServiceNodeExistException(key + " is exist, ", GatewayException.ExceptionCode.SERVICE_NODE_EXIST_EXCEPTION);
    }
  }

  public static void addServer(Service service) {
    Gateway gateway = (Gateway) Global.GATEWAY_CACHE.get(CacheName.CACHE_NAME_GATEWAY_CONFIG);
    gateway.addService(service);
    cacheService(service);
    startProxyServer(service);
  }

  public static void cacheKeepAliveChannel(Service service, Node node, Channel channel) {
    Map<String, Channel> channelMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_KEEPALIVE_CHANNEL_MAP);
    String key = getNodeKey(service, node);
    channelMap.put(key, channel);
  }

  public static Channel getKeepAliveChannel(Service service, Node node) {
    Map<String, Channel> channelMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_KEEPALIVE_CHANNEL_MAP);
    String key = getNodeKey(service, node);
    return channelMap.get(key);
  }

  public static boolean containsKeepAliveChannel(Service service, Node node) {
    Map<String, Channel> channelMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_KEEPALIVE_CHANNEL_MAP);
    String key = getNodeKey(service, node);
    return channelMap.containsKey(key);
  }

  public static Service getService(int port) {
    Map<Integer, Service> servicePortMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_PORT_MAP);
    return servicePortMap.get(port);
  }

  public static Service getService(String serviceName) {
    Map<String, Service> serviceMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_MAP);
    return serviceMap.get(serviceName);
  }

  public static String getNodeKey(Service service, Node node) {
    return service.getName() + "[" + node.getHost() + ":" + node.getPort() + "]";
  }

  public interface CacheName {
    String CACHE_NAME_GATEWAY_CONFIG = "gatewat-config";
    String CACHE_NAME_SERVICE_MAP = "service-mapping";
    String CACHE_NAME_SERVICE_PORT_MAP = "service-port-mapping";
    String CACHE_NAME_SERVICE_CHANNEL_MAP = "service-channel-mapping";
    String CACHE_NAME_SERVICE_NODE_MAP = "service-node-mapping";
    String CACHE_NAME_SERVICE_NODE_LIST = "service-node-list-";
    String CACHE_NAME_BOOTSTRAP_MAP = "bootstrap-mapping";

    String CACHE_NAME_KEEPALIVE_CHANNEL_MAP = "bootstrap-mapping";

    String CACHE_NAME_BOOTSTRAP_TYPE_HTTP = "http";
  }

  public static void startAllProxyServers() {
    Map<String, Service> serviceMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_MAP);
    serviceMap.entrySet().forEach(entry -> startProxyServer(entry.getValue()));
  }

  public static void startGatewayServer(Node gatewayConfig) {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(masterGroup, workerGroup).channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                channel.pipeline().addLast(new ProtobufDecoder(GatewayProto.Transfer.getDefaultInstance()));
                channel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                channel.pipeline().addLast(new ProtobufEncoder());
                channel.pipeline().addLast(new GatewayServerChannelHandler());
              }
            });
  }

  /**
   * shutdown service proxy
   *
   * @param service
   */
  public static boolean shutdownProxyServer(Service service) throws ExecutionException, InterruptedException {
    return shutdownProxyServer(service.getProxyPort());
  }

  /**
   * shutdown service port with proxy port
   *
   * @param port
   */
  public static boolean shutdownProxyServer(int port) throws ExecutionException, InterruptedException {
    Map<Integer, Channel> serviceChannelMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_CHANNEL_MAP);
    if (serviceChannelMap.containsKey(port)) {
      ChannelFuture future = serviceChannelMap.get(port).closeFuture().sync();
      log.info("shutdown port[" + port + "] --> " + future.isSuccess());
      return future.isSuccess();
    }
    return false;
  }

  public static void startProxyServer(Service service) {
    Map<Integer, Service> servicePortMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_PORT_MAP);
    Map<Integer, Channel> serviceChannelMap = Global.GATEWAY_CACHE.map(CacheName.CACHE_NAME_SERVICE_CHANNEL_MAP);
    if (servicePortMap.containsKey(service.getProxyPort())) {
      throw new ServicePortBindException("start proxy server[" + service.getName() + ":"
              + service.getProxyPort() + "] fail, this port is using.",
              GatewayException.ExceptionCode.SERVICE_BIND_FAIL);
    }
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
      bootstrap.bind(service.getProxyPort()).addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
          if (future.isSuccess()) {
            servicePortMap.put(service.getProxyPort(), service);
            serviceChannelMap.put(service.getProxyPort(), future.channel());
            log.info("start proxy server[" + service.getName() + ":" + service.getProxyPort() + "] success!!!");
          }
        }
      }).get();

    } catch (Exception e) {
      log.error("start proxy server[" + service.getName() + ":" + service.getProxyPort() + "] fail, "
              + e.getMessage(), e);
      throw new ServicePortBindException("start proxy server[" + service.getName() + ":"
              + service.getProxyPort() + "] fail, "
              + e.getMessage(), e,
              GatewayException.ExceptionCode.SERVICE_BIND_FAIL);
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
        } else {
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
    if (nodeList == null || nodeList.isEmpty()) {
      return null;
    }
    long index = requestTimes % nodeList.size();
    return nodeList.get((int) index);
  }

  public interface ChannelAttribute {
    AttributeKey<Channel> PROXY_CHANNEL = AttributeKey.newInstance("proxy_channel");
    AttributeKey<Channel> REQUEST_CHANNEL = AttributeKey.newInstance("request_channel");

    AttributeKey<Long> REQUEST_TIME_START = AttributeKey.newInstance("request_time_start");

    AttributeKey<Long> REQUEST_TIMES = AttributeKey.newInstance("request_times");
    AttributeKey<Long> REQUEST_ID = AttributeKey.newInstance("request_id");
    AttributeKey<String> REQUEST_URI = AttributeKey.newInstance("request_uri");

    AttributeKey<Service> SERVICE = AttributeKey.newInstance("service");
    AttributeKey<Node> NODE = AttributeKey.newInstance("node");
  }
}
