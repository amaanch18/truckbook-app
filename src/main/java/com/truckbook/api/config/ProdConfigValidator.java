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

    String databaseUrl = trimToNull(environment.getProperty("DATABASE_URL"));
    if (databaseUrl == null) {
      String host = trimToNull(environment.getProperty("DB_HOST"));
      String name = trimToNull(environment.getProperty("DB_NAME"));
      String user = trimToNull(environment.getProperty("DB_USER"));
      String pass = trimToNull(environment.getProperty("DB_PASSWORD"));
      if (host == null || name == null || user == null || pass == null) {
        errors.add("Missing database config: set DATABASE_URL or DB_HOST/DB_NAME/DB_USER/DB_PASSWORD.");
      }
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
