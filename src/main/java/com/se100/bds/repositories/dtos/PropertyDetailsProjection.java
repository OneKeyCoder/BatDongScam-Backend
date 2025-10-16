package com.se100.bds.repositories.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface PropertyDetailsProjection {
    UUID getId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();

    // Owner info
    UUID getOwnerId();
    String getOwnerFirstName();
    String getOwnerLastName();
    String getOwnerPhoneNumber();
    LocalDateTime getOwnerCreatedAt();
    LocalDateTime getOwnerUpdatedAt();

    // Agent info
    UUID getAgentId();
    String getAgentFirstName();
    String getAgentLastName();
    String getAgentPhoneNumber();
    LocalDateTime getAgentCreatedAt();
    LocalDateTime getAgentUpdatedAt();

    // Property info
    BigDecimal getServiceFeeAmount();
    UUID getPropertyTypeId();
    String getPropertyTypeName();
    UUID getWardId();
    String getWardName();
    UUID getDistrictId();
    String getDistrictName();
    UUID getCityId();
    String getCityName();
    String getTitle();
    String getDescription();
    String getTransactionType();
    String getFullAddress();
    BigDecimal getArea();
    Integer getRooms();
    Integer getBathrooms();
    Integer getFloors();
    Integer getBedrooms();
    String getHouseOrientation();
    String getBalconyOrientation();
    Integer getYearBuilt();
    BigDecimal getPriceAmount();
    BigDecimal getPricePerSquareMeter();
    BigDecimal getCommissionRate();
    String getAmenities();
    String getStatus();
    Integer getViewCount();
    LocalDateTime getApprovedAt();
}

