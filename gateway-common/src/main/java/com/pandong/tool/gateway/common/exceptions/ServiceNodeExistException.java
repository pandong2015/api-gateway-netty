package com.pandong.tool.gateway.common.exceptions;

public class ServiceNodeExistException extends GatewayException {

  public ServiceNodeExistException(ExceptionCode code) {
    super(code.getMsg(), code);
  }

  public ServiceNodeExistException(String s, ExceptionCode code) {
    super(s, code);
  }

  public ServiceNodeExistException(String s, Throwable throwable, ExceptionCode code) {
    super(s, throwable, code);
  }

  public ServiceNodeExistException(Throwable throwable, ExceptionCode code) {
    super(code.getMsg(), throwable, code);
  }
}
