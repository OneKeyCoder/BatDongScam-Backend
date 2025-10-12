package com.se100.bds.services.domains.property;

import com.se100.bds.entities.property.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PropertyService {
    Page<Property> getAll(Pageable pageable);
    Page<Property> getAllCardsWithFilters(List<UUID> cityIds, List<UUID> districtIds, List<UUID> wardIds,
                                     List<UUID> propertyTypeIds, BigDecimal minPrice, BigDecimal maxPrice, BigDecimal totalArea,
                                     int rooms, int bathrooms, int bedrooms, int floors, String houseOrientation, String balconyOrientation,
                                     String transactionType,
                                     Pageable pageable);
}
