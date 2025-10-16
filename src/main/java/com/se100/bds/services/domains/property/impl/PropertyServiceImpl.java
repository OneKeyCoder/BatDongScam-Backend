package com.se100.bds.services.domains.property.impl;

import com.se100.bds.dtos.responses.property.MediaResponse;
import com.se100.bds.dtos.responses.property.PropertyDetails;
import com.se100.bds.dtos.responses.user.SimpleUserResponse;
import com.se100.bds.models.entities.property.Property;
import com.se100.bds.models.entities.property.PropertyType;
import com.se100.bds.models.entities.user.User;
import com.se100.bds.mappers.PropertyMapper;
import com.se100.bds.repositories.domains.property.PropertyRepository;
import com.se100.bds.repositories.domains.property.PropertyTypeRepository;
import com.se100.bds.repositories.dtos.MediaProjection;
import com.se100.bds.repositories.dtos.PropertyCardProtection;
import com.se100.bds.repositories.dtos.PropertyDetailsProjection;
import com.se100.bds.services.domains.customer.CustomerFavoriteService;
import com.se100.bds.services.domains.property.PropertyService;
import com.se100.bds.services.domains.search.SearchService;
import com.se100.bds.services.domains.user.UserService;
import com.se100.bds.services.dtos.results.PropertyCard;
import com.se100.bds.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final CustomerFavoriteService customerFavoriteService;
    private final PropertyMapper propertyMapper;
    private final UserService userService;
    private final SearchService searchService;

    @Override
    public Page<Property> getAll(Pageable pageable) {
        return propertyRepository.findAll(pageable);
    }

    @Override
    public Page<PropertyCard> getAllCardsWithFilters(List<UUID> cityIds, List<UUID> districtIds, List<UUID> wardIds,
                                                     List<UUID> propertyTypeIds, BigDecimal minPrice, BigDecimal maxPrice, BigDecimal totalArea,
                                                     Integer rooms, Integer bathrooms, Integer bedrooms, Integer floors,
                                                     String houseOrientation, String balconyOrientation, String transactionType, int topK,
                                                     Pageable pageable) {

        User currentUser = null;
        try {
            currentUser = userService.getUser();
            searchService.addSearchList(currentUser.getId(), cityIds, districtIds, wardIds, propertyTypeIds);
        } catch (Exception ignored) {
        }

        if (topK > 0) {
            // TODO: Implement most popular Search for type
        }

        Page<PropertyCardProtection> cardProtections = propertyRepository.findAllPropertyCardsWithFilter(
                pageable,
                cityIds,
                districtIds,
                wardIds,
                propertyTypeIds,
                minPrice,
                maxPrice,
                totalArea,
                rooms,
                bathrooms,
                bedrooms,
                floors,
                houseOrientation,
                balconyOrientation,
                transactionType,
                currentUser != null ? currentUser.getId() : null
        );

        if (currentUser == null) {
            return propertyMapper.mapToPage(cardProtections, PropertyCard.class);
        }


        for (PropertyCardProtection card : cardProtections) {
            if (customerFavoriteService.isLike(card.getId(), currentUser.getId(), Constants.LikeTypeEnum.PROPERTY)) {
                card.setFavorite(true);
            }
        }

        return propertyMapper.mapToPage(cardProtections, PropertyCard.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyType> getAllTypes(Pageable pageable) {
        return propertyTypeRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyDetails getPropertyDetailsById(UUID propertyId) {
        PropertyDetailsProjection projection = propertyRepository.findPropertyDetailsById(propertyId);
        if (projection == null) {
            throw new RuntimeException("Property not found with id: " + propertyId);
        }

        // Get media list
        List<MediaProjection> mediaProjections = propertyRepository.findMediaByPropertyId(propertyId);

        // Map media projections to DTOs
        List<MediaResponse> mediaResponses = mediaProjections.stream()
                .map(media -> MediaResponse.builder()
                        .id(media.getId())
                        .createdAt(media.getCreatedAt())
                        .updatedAt(media.getUpdatedAt())
                        .mediaType(media.getMediaType())
                        .fileName(media.getFileName())
                        .filePath(media.getFilePath())
                        .mimeType(media.getMimeType())
                        .documentType(media.getDocumentType())
                        .build())
                .collect(Collectors.toList());

        // Map projection to DTO
        return PropertyDetails.builder()
                .id(projection.getId())
                .createdAt(projection.getCreatedAt())
                .updatedAt(projection.getUpdatedAt())
                .owner(projection.getOwnerId() != null ? SimpleUserResponse.builder()
                        .id(projection.getOwnerId())
                        .firstName(projection.getOwnerFirstName())
                        .lastName(projection.getOwnerLastName())
                        .fullName(projection.getOwnerFirstName() + " " + projection.getOwnerLastName())
                        .phoneNumber(projection.getOwnerPhoneNumber())
                        .createdAt(projection.getOwnerCreatedAt())
                        .updatedAt(projection.getOwnerUpdatedAt())
                        .build() : null)
                .assignedAgent(projection.getAgentId() != null ? SimpleUserResponse.builder()
                        .id(projection.getAgentId())
                        .firstName(projection.getAgentFirstName())
                        .lastName(projection.getAgentLastName())
                        .fullName(projection.getAgentFirstName() + " " + projection.getAgentLastName())
                        .phoneNumber(projection.getAgentPhoneNumber())
                        .createdAt(projection.getAgentCreatedAt())
                        .updatedAt(projection.getAgentUpdatedAt())
                        .build() : null)
                .serviceFeeAmount(projection.getServiceFeeAmount())
                .propertyTypeId(projection.getPropertyTypeId())
                .propertyTypeName(projection.getPropertyTypeName())
                .wardId(projection.getWardId())
                .wardName(projection.getWardName())
                .districtId(projection.getDistrictId())
                .districtName(projection.getDistrictName())
                .cityId(projection.getCityId())
                .cityName(projection.getCityName())
                .title(projection.getTitle())
                .description(projection.getDescription())
                .transactionType(projection.getTransactionType())
                .fullAddress(projection.getFullAddress())
                .area(projection.getArea())
                .rooms(projection.getRooms())
                .bathrooms(projection.getBathrooms())
                .floors(projection.getFloors())
                .bedrooms(projection.getBedrooms())
                .houseOrientation(projection.getHouseOrientation())
                .balconyOrientation(projection.getBalconyOrientation())
                .yearBuilt(projection.getYearBuilt())
                .priceAmount(projection.getPriceAmount())
                .pricePerSquareMeter(projection.getPricePerSquareMeter())
                .commissionRate(projection.getCommissionRate())
                .amenities(projection.getAmenities())
                .status(projection.getStatus())
                .viewCount(projection.getViewCount())
                .approvedAt(projection.getApprovedAt())
                .mediaList(mediaResponses)
                .build();
    }
}