package com.truckbook.api.exception;

public class BadGatewayException extends RuntimeException {
  public BadGatewayException(String message) {
    super(message);
  }
}
