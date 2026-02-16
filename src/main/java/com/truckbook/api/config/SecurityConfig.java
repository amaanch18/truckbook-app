package com.truckbook.api.config;

import com.truckbook.api.security.AdminKeyFilter;
import com.truckbook.api.security.JwtAuthFilter;
import com.truckbook.api.security.SubscriptionGuardFilter;
import com.truckbook.api.security.RestAccessDeniedHandler;
import com.truckbook.api.security.RestAuthEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
  private final JwtAuthFilter jwtAuthFilter;
  private final AdminKeyFilter adminKeyFilter;
  private final SubscriptionGuardFilter subscriptionGuardFilter;
  private final RestAuthEntryPoint restAuthEntryPoint;
  private final RestAccessDeniedHandler restAccessDeniedHandler;

  public SecurityConfig(
      JwtAuthFilter jwtAuthFilter,
      AdminKeyFilter adminKeyFilter,
      SubscriptionGuardFilter subscriptionGuardFilter,
      RestAuthEntryPoint restAuthEntryPoint,
      RestAccessDeniedHandler restAccessDeniedHandler) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.adminKeyFilter = adminKeyFilter;
    this.subscriptionGuardFilter = subscriptionGuardFilter;
    this.restAuthEntryPoint = restAuthEntryPoint;
    this.restAccessDeniedHandler = restAccessDeniedHandler;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * DEV: allow dev endpoints without auth so you can test DB + seed easily.
   * Everything else stays protected.
   */
  @Bean
  @Profile("dev")
  SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        // API-style app: no server sessions
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Disable CSRF for API (especially while you’re not using cookies)
        .csrf(csrf -> csrf.disable())
        .cors(cors -> {})

        // IMPORTANT: stop the default HTML login page
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(restAuthEntryPoint)
            .accessDeniedHandler(restAccessDeniedHandler))

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/admin/**").permitAll()
            .requestMatchers("/api/dev/**").permitAll()
            .requestMatchers("/error").permitAll()
            // optional: if you add actuator
            .requestMatchers("/actuator/health").permitAll()
            // keep everything else protected for now
            .anyRequest().authenticated()
        )
        .addFilterBefore(adminKeyFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(subscriptionGuardFilter, JwtAuthFilter.class);

    return http.build();
  }

  /**
   * NON-DEV: keep things protected by default.
   * Later, when OTP is implemented, we will permit /api/auth/** here too.
   */
  @Bean
  @Profile("!dev")
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .cors(cors -> {})
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(restAuthEntryPoint)
            .accessDeniedHandler(restAccessDeniedHandler))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/admin/**").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers("/actuator/health").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(adminKeyFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(subscriptionGuardFilter, JwtAuthFilter.class);

    return http.build();
  }
}
