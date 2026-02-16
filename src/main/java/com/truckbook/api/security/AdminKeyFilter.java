package com.truckbook.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AdminKeyFilter extends OncePerRequestFilter {
  private final String adminKey;
  private final ObjectMapper objectMapper;

  public AdminKeyFilter(
      @Value("${truckbook.admin.key}") String adminKey,
      ObjectMapper objectMapper) {
    this.adminKey = adminKey;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String provided = request.getHeader("X-Admin-Key");
    if (provided == null || !provided.equals(adminKey)) {
      writeUnauthorized(response);
      return;
    }

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return path == null || !path.startsWith("/api/admin/");
  }

  private void writeUnauthorized(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    Map<String, Object> body = new HashMap<>();
    body.put("error", "UNAUTHORIZED");
    body.put("message", "Invalid admin key");
    objectMapper.writeValue(response.getWriter(), body);
  }
}
