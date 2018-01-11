package com.pandong.tool.gateway.server.handlers;

import com.google.protobuf.ByteString;
import com.pandong.tool.gateway.common.*;
import com.pandong.tool.gateway.common.exceptions.GatewayException;
import com.pandong.tool.gateway.common.model.*;
import com.pandong.tool.gateway.server.utils.ServerUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class GatewayServerChannelHandler extends SimpleChannelInboundHandler<GatewayProto.Transfer> {
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, GatewayProto.Transfer msg) throws Exception {
    log.info("remoteAddress --> " + ctx.channel().remoteAddress() + " -- " + msg.getOperation().name());
    switch (msg.getOperation()) {
      case HEARTBEAT:
        heartbeat(ctx, msg);
        break;
      case REGISTER:
        register(ctx, msg);
        break;
      default:
        break;
    }
  }

  private void heartbeat(ChannelHandlerContext ctx, GatewayProto.Transfer msg) {

  }

  private void register(ChannelHandlerContext ctx, GatewayProto.Transfer msg) {
    String data = msg.getData().toStringUtf8();
    log.info("registre new client --> " + data);
    GatewayClient client = Global.paresClientConfig(data);
    Node node = client.getService();
    Service service = ServerUtil.getService(node.getServiceName());
    boolean isNewService = false;
    if (service == null) {
      service = new Service();
      service.setName(node.getServiceName());
      service.setProxyPort(node.getPort());
      isNewService = true;
    }
    service.addNode(node);
    OperateResonse resonse = null;
    try {
      if (isNewService) {
        ServerUtil.addServer(service);
      } else {
        ServerUtil.refreshServiceNode(service, node);
      }
      resonse = OperateResonse.builder().status(0).type(GatewayProto.OperationType.REGISTER).build();
    } catch (GatewayException e) {
      log.error(e.getMessage(), e);
      resonse = OperateResonse.builder().status(1).type(GatewayProto.OperationType.REGISTER).message(e.getMessage()).build();
    }
    String responseStr = Global.object2Xml(resonse);
    GatewayProto.Transfer transfer = msg.newBuilderForType()
            .clearData()
            .setData(ByteString.copyFrom(responseStr, Charset.forName("UTF-8")))
            .build();
    ctx.writeAndFlush(transfer);
  }
}
