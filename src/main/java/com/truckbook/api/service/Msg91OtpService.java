package com.truckbook.api.service;

import com.truckbook.api.exception.BadGatewayException;
import com.truckbook.api.exception.BadRequestException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Msg91OtpService {
  private static final Logger log = LoggerFactory.getLogger(Msg91OtpService.class);
  private final String authKey;
  private final String templateId;
  private final String senderId;
  private final String route;
  private final RestTemplate restTemplate;

  public Msg91OtpService(
      @Value("${msg91.auth-key:}") String authKey,
      @Value("${msg91.template-id:}") String templateId,
      @Value("${msg91.sender-id:}") String senderId,
      @Value("${msg91.route:4}") String route) {
    this.authKey = authKey;
    this.templateId = templateId;
    this.senderId = senderId;
    this.route = route;
    this.restTemplate = new RestTemplate();
  }

  public void sendOtp(String phone) {
    ensureConfigured();
    String url = "https://api.msg91.com/api/v5/otp";

    HttpHeaders headers = new HttpHeaders();
    headers.set("authkey", authKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> body = new HashMap<>();
    body.put("mobile", phone.replace("+", ""));
    body.put("template_id", templateId);
    body.put("sender", senderId);
    body.put("route", route);
    body.put("otp_length", 6);
    body.put("otp_expiry", 5);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
    log.info("MSG91 send OTP response: status={} body={}", response.getStatusCode().value(), response.getBody());

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new BadGatewayException("OTP_PROVIDER_FAILED");
    }
  }

  public void verifyOtp(String phone, String otp) {
    ensureConfigured();
    String url = "https://api.msg91.com/api/v5/otp/verify";

    HttpHeaders headers = new HttpHeaders();
    headers.set("authkey", authKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> body = new HashMap<>();
    body.put("mobile", phone.replace("+", ""));
    body.put("otp", otp);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
    log.info("MSG91 verify OTP response: status={} body={}", response.getStatusCode().value(), response.getBody());

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new BadRequestException("Invalid OTP");
    }
  }

  private void ensureConfigured() {
    if (authKey == null || authKey.isBlank()
        || templateId == null || templateId.isBlank()
        || senderId == null || senderId.isBlank()) {
      throw new IllegalStateException("MSG91 config is missing");
    }
  }
}
