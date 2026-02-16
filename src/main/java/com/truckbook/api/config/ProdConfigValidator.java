package com.truckbook.api.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdConfigValidator implements ApplicationRunner {
  private final Environment environment;

  public ProdConfigValidator(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void run(ApplicationArguments args) {
    List<String> errors = new ArrayList<>();

    String datasourceUrl = trimToNull(environment.getProperty("SPRING_DATASOURCE_URL"));
    String datasourceUser = trimToNull(environment.getProperty("SPRING_DATASOURCE_USERNAME"));
    String datasourcePass = trimToNull(environment.getProperty("SPRING_DATASOURCE_PASSWORD"));
    if (datasourceUrl == null || datasourceUser == null || datasourcePass == null) {
      errors.add("Missing database config: set SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD.");
    }

    String jwtSecret = trimToNull(environment.getProperty("JWT_SECRET"));
    if (jwtSecret == null) {
      jwtSecret = trimToNull(environment.getProperty("TRUCKBOOK_JWT_SECRET"));
    }
    if (jwtSecret == null) {
      jwtSecret = trimToNull(environment.getProperty("app.jwt.secret"));
    }
    if (jwtSecret == null) {
      errors.add("Missing JWT secret: set JWT_SECRET or TRUCKBOOK_JWT_SECRET.");
    }

    String adminKey = trimToNull(environment.getProperty("TRUCKBOOK_ADMIN_KEY"));
    if (adminKey == null) {
      adminKey = trimToNull(environment.getProperty("truckbook.admin.key"));
    }
    if (adminKey == null) {
      errors.add("Missing admin key: set TRUCKBOOK_ADMIN_KEY.");
    }

    String otpProvider = trimToNull(environment.getProperty("OTP_PROVIDER"));
    if (otpProvider == null) {
      otpProvider = "DEV";
    }
    if ("MSG91".equalsIgnoreCase(otpProvider)) {
    // OTP provider keys are optional for now (static OTP)
    } else if ("META_WHATSAPP".equalsIgnoreCase(otpProvider)) {
      String waToken = trimToNull(environment.getProperty("META_WA_TOKEN"));
      String waPhoneId = trimToNull(environment.getProperty("META_WA_PHONE_NUMBER_ID"));
      String waTemplate = trimToNull(environment.getProperty("META_WA_TEMPLATE"));
      if (waToken == null || waPhoneId == null || waTemplate == null) {
        errors.add("Missing Meta WhatsApp config: set META_WA_TOKEN, META_WA_PHONE_NUMBER_ID, META_WA_TEMPLATE.");
      }
    }

    if (!errors.isEmpty()) {
      throw new IllegalStateException(String.join(" ", errors));
    }
  }

  private static String trimToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
