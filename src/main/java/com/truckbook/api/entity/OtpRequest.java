package com.truckbook.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_requests", schema = "truckbook")
public class OtpRequest {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "org_id", nullable = false)
  private UUID orgId;

  @Column(name = "phone_e164", nullable = false)
  private String phoneE164;

  @Column(name = "otp_hash")
  private String otpHash;

  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @Column(name = "consumed_at")
  private OffsetDateTime consumedAt;

  @Column(name = "attempt_count", nullable = false)
  private Integer attemptCount;

  @Column(name = "last_sent_at")
  private OffsetDateTime lastSentAt;

  @Column(name = "send_count", nullable = false)
  private Integer sendCount;

  @Column(name = "provider")
  private String provider;

  @Column(name = "provider_ref")
  private String providerRef;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public OtpRequest() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getOrgId() {
    return orgId;
  }

  public void setOrgId(UUID orgId) {
    this.orgId = orgId;
  }

  public String getPhoneE164() {
    return phoneE164;
  }

  public void setPhoneE164(String phoneE164) {
    this.phoneE164 = phoneE164;
  }

  public String getOtpHash() {
    return otpHash;
  }

  public void setOtpHash(String otpHash) {
    this.otpHash = otpHash;
  }

  public OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(OffsetDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public OffsetDateTime getConsumedAt() {
    return consumedAt;
  }

  public void setConsumedAt(OffsetDateTime consumedAt) {
    this.consumedAt = consumedAt;
  }

  public Integer getAttemptCount() {
    return attemptCount;
  }

  public void setAttemptCount(Integer attemptCount) {
    this.attemptCount = attemptCount;
  }

  public OffsetDateTime getLastSentAt() {
    return lastSentAt;
  }

  public void setLastSentAt(OffsetDateTime lastSentAt) {
    this.lastSentAt = lastSentAt;
  }

  public Integer getSendCount() {
    return sendCount;
  }

  public void setSendCount(Integer sendCount) {
    this.sendCount = sendCount;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getProviderRef() {
    return providerRef;
  }

  public void setProviderRef(String providerRef) {
    this.providerRef = providerRef;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
