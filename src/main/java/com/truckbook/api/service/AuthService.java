package com.truckbook.api.service;

import com.truckbook.api.controller.dto.AuthResponse;

public interface AuthService {
  void requestOtp(String phoneE164);

  AuthResponse verifyOtp(String phoneE164, String otp);
}
