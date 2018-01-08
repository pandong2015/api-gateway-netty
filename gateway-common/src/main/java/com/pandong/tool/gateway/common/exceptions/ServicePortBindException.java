package com.pandong.tool.gateway.common.exceptions;

public class ServicePortBindException extends GatewayException {

  public ServicePortBindException(ExceptionCode code) {
    super(code.getMsg(), code);
  }

  public ServicePortBindException(String s, ExceptionCode code) {
    super(s, code);
  }

  public ServicePortBindException(String s, Throwable throwable, ExceptionCode code) {
    super(s, throwable, code);
  }

  public ServicePortBindException(Throwable throwable, ExceptionCode code) {
    super(code.getMsg(), throwable, code);
  }
}
