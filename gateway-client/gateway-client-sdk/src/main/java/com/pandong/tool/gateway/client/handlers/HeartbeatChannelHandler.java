package com.pandong.tool.gateway.client.handlers;

import com.google.protobuf.ByteString;
import com.pandong.tool.gateway.common.Global;
import com.pandong.tool.gateway.common.model.Heartbeat;
import com.pandong.tool.gateway.common.model.GatewayProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HeartbeatChannelHandler extends IdleStateHandler {
  public static final int DEFAULT_READER_IDLE_TIME_SECONDS = 120;
  public static final int DEFAULT_WRITER_IDLE_TIME_SECONDS = 60;
  public static final int DEFAULT_ALL_IDLE_TIME_SECONDS = 300;

  public HeartbeatChannelHandler() {
    this(DEFAULT_WRITER_IDLE_TIME_SECONDS);
  }

  public HeartbeatChannelHandler(int writerIdleTimeSeconds) {
    this(DEFAULT_READER_IDLE_TIME_SECONDS, writerIdleTimeSeconds);
  }

  public HeartbeatChannelHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds) {
    this(readerIdleTimeSeconds, writerIdleTimeSeconds, DEFAULT_ALL_IDLE_TIME_SECONDS);
  }

  public HeartbeatChannelHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
    super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.SECONDS);
  }

  @Override
  protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
    switch (evt.state()) {
      case WRITER_IDLE:
        sendHeartbeat(ctx);
        break;
      default:
        break;
    }
    super.channelIdle(ctx, evt);
  }

  private void sendHeartbeat(ChannelHandlerContext ctx) {
    Heartbeat heartbeat = Heartbeat.build();

    String data = Global.object2Xml(heartbeat);

    GatewayProto.Transfer.newBuilder()
            .setOperation(GatewayProto.OperationType.HEARTBEAT)
            .setData(ByteString.copyFrom(data, Global.DEFULT_CHARSET));
  }
}
