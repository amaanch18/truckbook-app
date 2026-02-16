package com.truckbook.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizations", schema = "truckbook")
public class Organization {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "onboarding_completed", nullable = false)
  private Boolean onboardingCompleted;

  @Column(name = "city")
  private String city;

  @Column(name = "owner_display_name")
  private String ownerDisplayName;

  public Organization() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Boolean getOnboardingCompleted() {
    return onboardingCompleted;
  }

  public void setOnboardingCompleted(Boolean onboardingCompleted) {
    this.onboardingCompleted = onboardingCompleted;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getOwnerDisplayName() {
    return ownerDisplayName;
  }

  public void setOwnerDisplayName(String ownerDisplayName) {
    this.ownerDisplayName = ownerDisplayName;
  }
}
