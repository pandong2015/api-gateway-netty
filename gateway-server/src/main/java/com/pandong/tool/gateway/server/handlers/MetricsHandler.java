package com.pandong.tool.gateway.server.handlers;

import com.pandong.tool.gateway.common.GatewayCounter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pandong
 */

@Slf4j
public class MetricsHandler extends ChannelDuplexHandler {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if(msg instanceof ByteBuf){
      GatewayCounter.streamByteCount(GatewayCounter.StreamType.READ, ((ByteBuf) msg).readableBytes());
    }
    super.channelRead(ctx, msg);
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    if(msg instanceof ByteBuf){
      GatewayCounter.streamByteCount(GatewayCounter.StreamType.WRITE, ((ByteBuf) msg).readableBytes());
    }
    super.write(ctx,msg,promise);
  }

}
