package com.pandong.tool.gateway.common.model;

import com.pandong.tool.gateway.common.model.GatewayProto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperateResonse {
  private GatewayProto.OperationType type;
  private int status;
  private String message;
}
