package com.se100.bds.helpers.domains;

import com.se100.bds.models.entities.location.City;
import com.se100.bds.models.entities.location.District;
import com.se100.bds.models.entities.location.Ward;
import com.se100.bds.models.entities.property.Property;
import com.se100.bds.models.entities.property.PropertyType;
import com.se100.bds.models.entities.user.User;
import com.se100.bds.models.schemas.search.SearchLog;
import com.se100.bds.repositories.domains.location.CityRepository;
import com.se100.bds.repositories.domains.location.DistrictRepository;
import com.se100.bds.repositories.domains.location.WardRepository;
import com.se100.bds.repositories.domains.mongo.search.SearchLogRepository;
import com.se100.bds.repositories.domains.property.PropertyRepository;
import com.se100.bds.repositories.domains.property.PropertyTypeRepository;
import com.se100.bds.repositories.domains.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchLogDummyData {

    private final SearchLogRepository searchLogRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final UserRepository userRepository;

    private final Random random = new Random();

    @Transactional(readOnly = true)
    public void createDummy() {
        log.info("Creating 100k search logs...");

        // Fetch all necessary data
        List<UUID> userIds = userRepository.findAll().stream().map(User::getId).toList();
        List<UUID> cityIds = cityRepository.findAll().stream().map(City::getId).toList();

        // Build simple data structures with IDs only
        List<LocationData> districtDataList = new ArrayList<>();
        for (District district : districtRepository.findAll()) {
            districtDataList.add(new LocationData(district.getId(), district.getCity().getId()));
        }

        List<LocationData> wardDataList = new ArrayList<>();
        for (Ward ward : wardRepository.findAll()) {
            wardDataList.add(new LocationData(ward.getId(), ward.getDistrict().getId()));
        }

        List<PropertyData> propertyDataList = new ArrayList<>();
        for (Property property : propertyRepository.findAll()) {
            propertyDataList.add(new PropertyData(
                property.getId(),
                property.getWard().getId(),
                property.getPropertyType() != null ? property.getPropertyType().getId() : null
            ));
        }

        List<UUID> propertyTypeIds = propertyTypeRepository.findAll().stream()
            .map(PropertyType::getId).toList();

        // Create lookup maps
        Map<UUID, UUID> districtToCityMap = new HashMap<>();
        for (LocationData data : districtDataList) {
            districtToCityMap.put(data.id, data.parentId);
        }

        Map<UUID, UUID> wardToDistrictMap = new HashMap<>();
        for (LocationData data : wardDataList) {
            wardToDistrictMap.put(data.id, data.parentId);
        }

        log.info("Found {} users, {} cities, {} districts, {} wards, {} properties, {} property types",
                userIds.size(), cityIds.size(), districtDataList.size(), wardDataList.size(),
                propertyDataList.size(), propertyTypeIds.size());

        // Generate 100k search logs in batches
        int totalLogs = 100000;
        int batchSize = 5000;
        int batches = totalLogs / batchSize;

        for (int batch = 0; batch < batches; batch++) {
            List<SearchLog> searchLogs = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                SearchLog searchLog = generateSearchLog(
                        userIds, cityIds, districtDataList, wardDataList,
                        propertyDataList, propertyTypeIds,
                        districtToCityMap, wardToDistrictMap
                );
                searchLogs.add(searchLog);
            }

            searchLogRepository.saveAll(searchLogs);
            log.info("Saved batch {}/{} ({} logs)", batch + 1, batches, (batch + 1) * batchSize);
        }

        log.info("Successfully created {} search logs", totalLogs);
    }

    private SearchLog generateSearchLog(
            List<UUID> userIds,
            List<UUID> cityIds,
            List<LocationData> districtDataList,
            List<LocationData> wardDataList,
            List<PropertyData> propertyDataList,
            List<UUID> propertyTypeIds,
            Map<UUID, UUID> districtToCityMap,
            Map<UUID, UUID> wardToDistrictMap
    ) {
        SearchLog.SearchLogBuilder builder = SearchLog.builder();

        // 50% chance to have a user (simulating both logged-in and guest searches)
        if (!userIds.isEmpty() && random.nextDouble() < 0.5) {
            builder.userId(userIds.get(random.nextInt(userIds.size())));
        }

        // Determine the search type with weighted probabilities
        double searchTypeRandom = random.nextDouble();

        if (searchTypeRandom < 0.3 && !propertyDataList.isEmpty()) {
            // 30% - Property search (most specific)
            PropertyData property = propertyDataList.get(random.nextInt(propertyDataList.size()));

            builder.propertyId(property.propertyId);

            UUID wardId = property.wardId;
            UUID districtId = wardToDistrictMap.get(wardId);
            UUID cityId = districtToCityMap.get(districtId);

            builder.wardId(wardId)
                   .districtId(districtId)
                   .cityId(cityId);

            // 50% chance to also filter by property type
            if (random.nextDouble() < 0.5 && property.propertyTypeId != null) {
                builder.propertyTypeId(property.propertyTypeId);
            }

        } else if (searchTypeRandom < 0.55 && !wardDataList.isEmpty()) {
            // 25% - Ward search (search by ward, include district and city)
            LocationData ward = wardDataList.get(random.nextInt(wardDataList.size()));

            builder.wardId(ward.id);

            UUID districtId = ward.parentId;
            UUID cityId = districtToCityMap.get(districtId);

            builder.districtId(districtId);
            if (cityId != null) {
                builder.cityId(cityId);
            }

            // 40% chance to also filter by property type
            if (!propertyTypeIds.isEmpty() && random.nextDouble() < 0.4) {
                builder.propertyTypeId(propertyTypeIds.get(random.nextInt(propertyTypeIds.size())));
            }

        } else if (searchTypeRandom < 0.8 && !districtDataList.isEmpty()) {
            // 25% - District search (search by district, include city, but NOT ward)
            LocationData district = districtDataList.get(random.nextInt(districtDataList.size()));

            builder.districtId(district.id);
            builder.cityId(district.parentId);

            // 40% chance to also filter by property type
            if (!propertyTypeIds.isEmpty() && random.nextDouble() < 0.4) {
                builder.propertyTypeId(propertyTypeIds.get(random.nextInt(propertyTypeIds.size())));
            }

        } else if (!cityIds.isEmpty()) {
            // 20% - City search (search by city only, NO district or ward)
            UUID cityId = cityIds.get(random.nextInt(cityIds.size()));
            builder.cityId(cityId);

            // 30% chance to also filter by property type
            if (!propertyTypeIds.isEmpty() && random.nextDouble() < 0.3) {
                builder.propertyTypeId(propertyTypeIds.get(random.nextInt(propertyTypeIds.size())));
            }
        }

        return builder.build();
    }

    public boolean any() {
        return searchLogRepository.count() > 0;
    }

    public void clear() {
        searchLogRepository.deleteAll();
        log.info("Cleared all search logs");
    }

    // Helper classes to avoid lazy loading issues
    private static class LocationData {
        UUID id;
        UUID parentId;

        LocationData(UUID id, UUID parentId) {
            this.id = id;
            this.parentId = parentId;
        }
    }

    private static class PropertyData {
        UUID propertyId;
        UUID wardId;
        UUID propertyTypeId;

        PropertyData(UUID propertyId, UUID wardId, UUID propertyTypeId) {
            this.propertyId = propertyId;
            this.wardId = wardId;
            this.propertyTypeId = propertyTypeId;
        }
    }
}
