package com.pandong.tool.gateway.common.exceptions;

import lombok.Data;

@Data
public class GatewayException extends RuntimeException {
  public enum ExceptionCode {
    SERVICE_BIND_FAIL(1001, "Servic port bind fail."),
    SERVICE_NODE_EXIST_EXCEPTION(1101, "Service node is exist.");

    private String msg;
    private int code;

    ExceptionCode(int code, String msg) {
      this.code = code;
      this.msg = msg;
    }

    public String getMsg() {
      return msg;
    }

    public int getCode() {
      return code;
    }
  }


  private ExceptionCode code;

  public GatewayException(ExceptionCode code) {
    super(code.msg);
    this.code = code;
  }

  public GatewayException(String s, ExceptionCode code) {
    super(s);
    this.code = code;
  }

  public GatewayException(String s, Throwable throwable, ExceptionCode code) {
    super(s, throwable);
    this.code = code;
  }

  public GatewayException(Throwable throwable, ExceptionCode code) {
    super(code.msg, throwable);
    this.code = code;
  }
}
