package com.truckbook.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OtpVerifyRequest {
  @NotBlank
  @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "Phone must be valid E.164")
  private String phoneE164;

  @NotBlank
  @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
  private String otp;

  public OtpVerifyRequest() {}

  public String getPhoneE164() {
    return phoneE164;
  }

  public void setPhoneE164(String phoneE164) {
    this.phoneE164 = phoneE164;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
