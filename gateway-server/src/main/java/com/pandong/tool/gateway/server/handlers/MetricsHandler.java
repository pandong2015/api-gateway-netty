package com.pandong.tool.gateway.server.handlers;

import com.pandong.common.generater.IdGenerate;
import com.pandong.common.units.StringUtils;
import com.pandong.tool.gateway.common.GatewayCounter;
import com.pandong.tool.gateway.common.Global;
import com.pandong.tool.gateway.common.model.Node;
import com.pandong.tool.gateway.common.model.Service;
import com.pandong.tool.gateway.server.utils.ServerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author pandong
 */

@Slf4j
public class MetricsHandler extends ChannelDuplexHandler {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    long requestId = ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_ID).get();
    if (msg instanceof ByteBuf) {
      int bytes = ((ByteBuf) msg).readableBytes();
      GatewayCounter.streamByteCount(GatewayCounter.StreamType.READ, bytes);
      GatewayCounter.requestByteCount(GatewayCounter.StreamType.READ, requestId, bytes);
    }
    super.channelRead(ctx, msg);
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

    long requestId = ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_ID).get();
    if (msg instanceof ByteBuf) {
      int bytes = ((ByteBuf) msg).readableBytes();
      GatewayCounter.streamByteCount(GatewayCounter.StreamType.WRITE, bytes);
      GatewayCounter.requestByteCount(GatewayCounter.StreamType.WRITE, requestId, bytes);

    }
    super.write(ctx, msg, promise);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().localAddress();
    Service service = ServerUtil.getService(inetSocketAddress.getPort());

    List<Node> nodeList = Global.GATEWAY_CACHE.list(ServerUtil.CacheName.CACHE_NAME_SERVICE_NODE_LIST + service.getName());
    long requestTimes = GatewayCounter.getServiceRequestCounter(service);
    long requestId = IdGenerate.generate(requestTimes);
    Node node = ServerUtil.loadBalancer(requestTimes, nodeList);

    ctx.channel().attr(ServerUtil.ChannelAttribute.SERVICE).set(service);
    ctx.channel().attr(ServerUtil.ChannelAttribute.NODE).set(node);
    ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_TIMES).set(requestTimes);
    ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_ID).set(requestId);
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("channelInactive...");
    Service service = ctx.channel().attr(ServerUtil.ChannelAttribute.SERVICE).get();
    Node node = ctx.channel().attr(ServerUtil.ChannelAttribute.NODE).get();
    String requestUri = ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_URI).get();
    long requestId = ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_ID).get();
    long readBytes = GatewayCounter.getRequestByteCount(GatewayCounter.StreamType.READ, requestId);
    long writeBytes = GatewayCounter.getRequestByteCount(GatewayCounter.StreamType.WRITE, requestId);
    if (service != null) {
      GatewayCounter.streamByteCount(GatewayCounter.StreamType.READ, service, readBytes);
      GatewayCounter.streamByteCount(GatewayCounter.StreamType.WRITE, service, writeBytes);
      if (node != null) {
        GatewayCounter.streamByteCount(GatewayCounter.StreamType.READ, service, node, readBytes);
        GatewayCounter.streamByteCount(GatewayCounter.StreamType.WRITE, service, node, writeBytes);
        if (!StringUtils.isNull(requestUri)) {
          GatewayCounter.streamByteCount(GatewayCounter.StreamType.READ, service, node, requestUri, readBytes);
          GatewayCounter.streamByteCount(GatewayCounter.StreamType.WRITE, service, node, requestUri, writeBytes);
        }
      }
    }


    GatewayCounter.cleanRequestByteCount(GatewayCounter.StreamType.READ, requestId);
    GatewayCounter.cleanRequestByteCount(GatewayCounter.StreamType.WRITE, requestId);

    ctx.channel().attr(ServerUtil.ChannelAttribute.SERVICE).set(null);
    ctx.channel().attr(ServerUtil.ChannelAttribute.NODE).set(null);
    ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_TIMES).set(null);
    ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_ID).set(null);
    ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_URI).set(null);
    ctx.channel().attr(ServerUtil.ChannelAttribute.REQUEST_CHANNEL).set(null);
    super.channelInactive(ctx);
  }
}
