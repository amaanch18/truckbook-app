package com.truckbook.api.controller.dto.reports;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class ReportRange {
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate from;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate to;

  private String groupBy;

  public ReportRange() {}

  public ReportRange(LocalDate from, LocalDate to, String groupBy) {
    this.from = from;
    this.to = to;
    this.groupBy = groupBy;
  }

  public LocalDate getFrom() {
    return from;
  }

  public void setFrom(LocalDate from) {
    this.from = from;
  }

  public LocalDate getTo() {
    return to;
  }

  public void setTo(LocalDate to) {
    this.to = to;
  }

  public String getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(String groupBy) {
    this.groupBy = groupBy;
  }
}
