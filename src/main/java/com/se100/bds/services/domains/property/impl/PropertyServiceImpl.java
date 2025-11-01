package com.se100.bds.services.domains.property.impl;

import com.se100.bds.dtos.responses.property.PropertyDetails;
import com.se100.bds.exceptions.NotFoundException;
import com.se100.bds.models.entities.property.Property;
import com.se100.bds.models.entities.property.PropertyType;
import com.se100.bds.models.entities.user.SaleAgent;
import com.se100.bds.models.entities.user.User;
import com.se100.bds.mappers.PropertyMapper;
import com.se100.bds.repositories.domains.property.PropertyRepository;
import com.se100.bds.repositories.domains.property.PropertyTypeRepository;
import com.se100.bds.repositories.dtos.MediaProjection;
import com.se100.bds.repositories.dtos.PropertyCardProtection;
import com.se100.bds.repositories.dtos.PropertyDetailsProjection;
import com.se100.bds.services.domains.property.PropertyService;
import com.se100.bds.services.domains.ranking.RankingService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final PropertyMapper propertyMapper;
    private final UserService userService;
    private final SearchService searchService;
    private final RankingService rankingService;

    @Override
    public Page<Property> getAll(Pageable pageable) {
        return propertyRepository.findAll(pageable);
    }

    @Override
    public Page<PropertyCard> getAllCardsWithFilters(List<UUID> cityIds, List<UUID> districtIds, List<UUID> wardIds,
                                                     List<UUID> propertyTypeIds, UUID ownerId, String ownerName,
                                                     List<Constants.ContributionTierEnum> ownerTier,
                                                     UUID agentId, String agentName, List<Constants.PerformanceTierEnum> agentTier,
                                                     BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minArea, BigDecimal maxArea,
                                                     Integer rooms, Integer bathrooms, Integer bedrooms, Integer floors,
                                                     String houseOrientation, String balconyOrientation,
                                                     List<Constants.TransactionTypeEnum> transactionType,
                                                     List<Constants.PropertyStatusEnum> statuses, boolean topK,
                                                     Pageable pageable) {

        User currentUser = null;
        try {
            currentUser = userService.getUser();
            searchService.addSearchList(currentUser.getId(), cityIds, districtIds, wardIds, propertyTypeIds);
        } catch (Exception ignored) {
        }

        List<UUID> propertyIds = null;
        if (topK) {
            // Lấy tháng và năm hiện tại
            int currentYear = java.time.LocalDateTime.now().getYear();
            int currentMonth = java.time.LocalDateTime.now().getMonthValue();

            propertyIds = searchService.getMostSearchedPropertyIds(1000, currentYear, currentMonth);
            log.info("Found {} most searched properties", propertyIds.size());
        }

        List<String> transactionTypeStrings = null;
        if (transactionType != null && !transactionType.isEmpty()) {
            transactionTypeStrings = transactionType.stream()
                    .map(Enum::name)
                    .toList();
        }

        List<UUID> ownerIds;
        if (ownerId != null) {
            ownerIds = List.of(ownerId);
        } else {
            String searchName = ownerName != null ? ownerName : "";
            List<User> owners = userService.getAllByName(searchName);

            if (ownerTier != null && !ownerTier.isEmpty()) {
                int currentMonth = LocalDateTime.now().getMonthValue();
                int currentYear = LocalDateTime.now().getYear();

                ownerIds = owners.stream()
                        .map(User::getId)
                        .filter(id -> {
                            String tier = rankingService.getTier(id, Constants.RoleEnum.PROPERTY_OWNER, currentMonth, currentYear);
                            return tier != null && ownerTier.stream().anyMatch(filter -> filter.name().equals(tier));
                        })
                        .toList();
            } else {
                ownerIds = owners.stream()
                        .map(User::getId)
                        .toList();
            }
        }

        List<UUID> agentIds;
        if (agentId != null) {
            agentIds = List.of(agentId);
        }  else {
            String searchName = agentName != null ? agentName : "";
            List<User> agents = userService.getAllByName(searchName);
            if (agentTier != null && !agentTier.isEmpty()) {
                int currentMonth = LocalDateTime.now().getMonthValue();
                int currentYear = LocalDateTime.now().getYear();

                agentIds = agents.stream()
                        .map(User::getId)
                        .filter(id -> {
                            String tier = rankingService.getTier(id, Constants.RoleEnum.SALESAGENT, currentMonth, currentYear);
                            return tier != null && agentTier.stream().anyMatch(filter -> filter.name().equals(tier));
                        })
                        .toList();
            } else  {
                agentIds = agents.stream()
                        .map(User::getId)
                        .toList();
            }
        }

        List<String> statusStrings = null;
        if (statuses != null && !statuses.isEmpty()) {
            statusStrings = statuses.stream()
                    .map(Enum::name)
                    .toList();
        }

        Page<PropertyCardProtection> cardProtections = propertyRepository.findAllPropertyCardsWithFilter(
                pageable,
                propertyIds,
                cityIds,
                districtIds,
                wardIds,
                propertyTypeIds,
                ownerIds,
                agentIds,
                minPrice,
                maxPrice,
                minArea,
                maxArea,
                rooms,
                bathrooms,
                bedrooms,
                floors,
                houseOrientation,
                balconyOrientation,
                transactionTypeStrings,
                statusStrings,
                currentUser != null ? currentUser.getId() : null
        );

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

        // Use mapper to convert projection to DTO
        return propertyMapper.toPropertyDetails(projection, mediaProjections);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Property> getAllByUserIdAndStatus(UUID ownerId, UUID customerId, UUID salesAgentId, List<Constants.PropertyStatusEnum> statuses) {
        if (customerId == null) {
            if (salesAgentId == null) {
                return statuses == null || statuses.isEmpty() ? propertyRepository.findAllByOwner_Id(ownerId) : propertyRepository.findAllByOwner_IdAndStatusIn(ownerId, statuses);
            }
            else if (ownerId == null) {
                return statuses == null || statuses.isEmpty() ? propertyRepository.findAllByAssignedAgent_Id(salesAgentId) : propertyRepository.findAllByAssignedAgent_IdAndStatusIn(salesAgentId, statuses);
            }
            return statuses == null || statuses.isEmpty() ? propertyRepository.findAllByOwner_IdAndAssignedAgent_Id(ownerId, salesAgentId) : propertyRepository.findAllByOwner_IdAndAssignedAgent_IdAndStatusIn(ownerId, salesAgentId, statuses);
        } else {
            return propertyRepository.findAllByCustomer_IdAndStatusIn(customerId, statuses);
        }
    }

    @Override
    @Transactional
    public void assignAgentToProperty(UUID agentId, UUID propertyId) {
        SaleAgent salesAgent = userService.findSaleAgentById(agentId);
        Property assignedProperty = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("Property not found with id: " + propertyId));
        assignedProperty.setAssignedAgent(salesAgent);

    }
}