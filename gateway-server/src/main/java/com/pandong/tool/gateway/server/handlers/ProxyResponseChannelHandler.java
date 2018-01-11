package com.pandong.tool.gateway.server.handlers;

import com.pandong.tool.gateway.common.model.Node;
import com.pandong.tool.gateway.common.model.Service;
import com.pandong.tool.gateway.server.utils.ServerUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pandong
 */

@Slf4j
public class ProxyResponseChannelHandler extends SimpleChannelInboundHandler<HttpObject> {
  @Override
  public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    Channel requestChannel = ctx.channel();
    Channel proxyChannel = requestChannel.attr(ServerUtil.ChannelAttribute.PROXY_CHANNEL).get();
    if (proxyChannel == null) {
      requestChannel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
    }
    if (msg instanceof HttpResponse) {
      log.debug("proxy response code --> " + ((HttpResponse) msg).status().toString());
      HttpResponse response = new DefaultHttpResponse(((HttpResponse) msg).protocolVersion(),
              ((HttpResponse) msg).status(), ((HttpResponse) msg).headers());
      proxyChannel.writeAndFlush(response);
      String connection = ((HttpResponse) msg).headers().get(HttpHeaderNames.CONNECTION);
      if (HttpHeaderValues.KEEP_ALIVE.contentEquals(connection)){
        Service service = requestChannel.attr(ServerUtil.ChannelAttribute.SERVICE).get();
        Node node = requestChannel.attr(ServerUtil.ChannelAttribute.NODE).get();
        ServerUtil.cacheKeepAliveChannel(service, node, requestChannel);
      }
    } else if (msg instanceof HttpContent) {
      if (msg instanceof LastHttpContent) {
        proxyChannel.writeAndFlush(((HttpContent) msg).copy()).addListener(ChannelFutureListener.CLOSE);
        requestChannel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
        long startTime = proxyChannel.attr(ServerUtil.ChannelAttribute.REQUEST_TIME_START).get();
        log.info("proxy request exec time -->" + (System.currentTimeMillis() - startTime)+" ms");
      } else {
        proxyChannel.writeAndFlush(((HttpContent) msg).copy());
      }
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    ctx.channel().attr(ServerUtil.ChannelAttribute.PROXY_CHANNEL).set(null);
    super.channelInactive(ctx);
  }
}
