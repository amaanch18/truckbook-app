package com.truckbook.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_throttle", schema = "truckbook")
public class OtpThrottle {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "phone_e164", nullable = false)
  private String phoneE164;

  @Column(name = "sent_at", nullable = false)
  private OffsetDateTime sentAt;

  @Column(name = "provider")
  private String provider;

  public OtpThrottle() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getPhoneE164() {
    return phoneE164;
  }

  public void setPhoneE164(String phoneE164) {
    this.phoneE164 = phoneE164;
  }

  public OffsetDateTime getSentAt() {
    return sentAt;
  }

  public void setSentAt(OffsetDateTime sentAt) {
    this.sentAt = sentAt;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }
}
