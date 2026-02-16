package com.truckbook.api.exception;

public class BadRequestException extends RuntimeException {
  private final java.util.Map<String, String> fields;

  public BadRequestException(String message) {
    super(message);
    this.fields = null;
  }

  public BadRequestException(String message, java.util.Map<String, String> fields) {
    super(message);
    this.fields = fields;
  }

  public java.util.Map<String, String> getFields() {
    return fields;
  }
}
