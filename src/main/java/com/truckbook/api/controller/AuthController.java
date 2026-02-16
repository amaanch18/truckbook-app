package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.AuthResponse;
import com.truckbook.api.controller.dto.OtpRequestRequest;
import com.truckbook.api.controller.dto.OtpVerifyRequest;
import com.truckbook.api.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/otp/request")
  public Map<String, String> requestOtp(@Valid @RequestBody OtpRequestRequest request) {
    authService.requestOtp(request.getPhoneE164());
    return Map.of("status", "sent");
  }

  @PostMapping("/otp/verify")
  public AuthResponse verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
    return authService.verifyOtp(request.getPhoneE164(), request.getOtp());
  }
}
