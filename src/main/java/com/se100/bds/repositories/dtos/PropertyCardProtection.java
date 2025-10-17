package com.se100.bds.repositories.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record PropertyCardProtection(
    UUID id,
    String title,
    String thumbnailUrl,
    boolean isFavorite,
    int numberOfImages,
    String address,
    String district,
    String city,
    String status,
    BigDecimal price,
    BigDecimal totalArea
) {}
