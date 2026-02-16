package com.truckbook.api.exception;

public class ApiError {
  private String error;
  private java.util.Map<String, String> fields;
  private String path;
  private String timestamp;

  public ApiError() {}

  public ApiError(String error, String path, String timestamp) {
    this.error = error;
    this.path = path;
    this.timestamp = timestamp;
  }

  public ApiError(String error, java.util.Map<String, String> fields, String path, String timestamp) {
    this.error = error;
    this.fields = fields;
    this.path = path;
    this.timestamp = timestamp;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public java.util.Map<String, String> getFields() {
    return fields;
  }

  public void setFields(java.util.Map<String, String> fields) {
    this.fields = fields;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}
