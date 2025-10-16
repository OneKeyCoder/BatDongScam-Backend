package com.se100.bds.mappers;

import com.se100.bds.dtos.responses.property.MediaResponse;
import com.se100.bds.dtos.responses.property.PropertyDetails;
import com.se100.bds.dtos.responses.property.SimplePropertyCard;
import com.se100.bds.dtos.responses.user.SimpleUserResponse;
import com.se100.bds.models.entities.property.Property;
import com.se100.bds.services.dtos.results.PropertyCard;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PropertyMapper extends BaseMapper {
    @Autowired
    public PropertyMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    protected void configureCustomMappings() {
        // Existing mapping for PropertyCard to SimplePropertyCard
        modelMapper.typeMap(PropertyCard.class, SimplePropertyCard.class)
                .addMappings(mapper -> {
                    mapper.using(ctx -> {
                        PropertyCard propertyCard = (PropertyCard) ctx.getSource();
                        String district = propertyCard.getDistrict();
                        String city = propertyCard.getCity();

                        if (district != null && city != null) {
                            return district + ", " + city;
                        } else if (city != null) {
                            return city;
                        }
                        return district;
                    }).map(src -> src, (dest, value) -> dest.setLocation((String) value));
                });

        // Custom mapping for Property to PropertyDetails
        modelMapper.typeMap(Property.class, PropertyDetails.class)
                .addMappings(mapper -> {
                    // Map owner
                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        if (property.getOwner() != null && property.getOwner().getUser() != null) {
                            var user = property.getOwner().getUser();
                            return SimpleUserResponse.builder()
                                    .id(user.getId())
                                    .firstName(user.getFirstName())
                                    .lastName(user.getLastName())
                                    .fullName(user.getFirstName() + " " + user.getLastName())
                                    .phoneNumber(user.getPhoneNumber())
                                    .createdAt(user.getCreatedAt())
                                    .updatedAt(user.getUpdatedAt())
                                    .build();
                        }
                        return null;
                    }).map(src -> src, PropertyDetails::setOwner);

                    // Map assigned agent
                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        if (property.getAssignedAgent() != null && property.getAssignedAgent().getUser() != null) {
                            var user = property.getAssignedAgent().getUser();
                            return SimpleUserResponse.builder()
                                    .id(user.getId())
                                    .firstName(user.getFirstName())
                                    .lastName(user.getLastName())
                                    .fullName(user.getFirstName() + " " + user.getLastName())
                                    .phoneNumber(user.getPhoneNumber())
                                    .createdAt(user.getCreatedAt())
                                    .updatedAt(user.getUpdatedAt())
                                    .build();
                        }
                        return null;
                    }).map(src -> src, PropertyDetails::setAssignedAgent);

                    // Map property type
                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getPropertyType() != null ? property.getPropertyType().getId() : null;
                    }).map(src -> src, PropertyDetails::setPropertyTypeId);

                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getPropertyType() != null ? property.getPropertyType().getTypeName() : null;
                    }).map(src -> src, PropertyDetails::setPropertyTypeName);

                    // Map location hierarchy
                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getWard() != null ? property.getWard().getId() : null;
                    }).map(src -> src, PropertyDetails::setWardId);

                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getWard() != null ? property.getWard().getWardName() : null;
                    }).map(src -> src, PropertyDetails::setWardName);

                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getWard() != null && property.getWard().getDistrict() != null
                                ? property.getWard().getDistrict().getId() : null;
                    }).map(src -> src, PropertyDetails::setDistrictId);

                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getWard() != null && property.getWard().getDistrict() != null
                                ? property.getWard().getDistrict().getDistrictName() : null;
                    }).map(src -> src, PropertyDetails::setDistrictName);

                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getWard() != null && property.getWard().getDistrict() != null
                                && property.getWard().getDistrict().getCity() != null
                                ? property.getWard().getDistrict().getCity().getId() : null;
                    }).map(src -> src, PropertyDetails::setCityId);

                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getWard() != null && property.getWard().getDistrict() != null
                                && property.getWard().getDistrict().getCity() != null
                                ? property.getWard().getDistrict().getCity().getCityName() : null;
                    }).map(src -> src, PropertyDetails::setCityName);

                    // Map enums to strings
                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getTransactionType() != null ? property.getTransactionType().name() : null;
                    }).map(src -> src, PropertyDetails::setTransactionType);

                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getHouseOrientation() != null ? property.getHouseOrientation().name() : null;
                    }).map(src -> src, PropertyDetails::setHouseOrientation);

                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getBalconyOrientation() != null ? property.getBalconyOrientation().name() : null;
                    }).map(src -> src, PropertyDetails::setBalconyOrientation);

                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        return property.getStatus() != null ? property.getStatus().name() : null;
                    }).map(src -> src, PropertyDetails::setStatus);

                    // Map media list
                    mapper.using(ctx -> {
                        Property property = (Property) ctx.getSource();
                        if (property.getMediaList() != null && !property.getMediaList().isEmpty()) {
                            return property.getMediaList().stream()
                                    .map(media -> MediaResponse.builder()
                                            .id(media.getId())
                                            .mediaType(media.getMediaType() != null ? media.getMediaType().name() : null)
                                            .fileName(media.getFileName())
                                            .filePath(media.getFilePath())
                                            .mimeType(media.getMimeType())
                                            .documentType(media.getDocumentType())
                                            .createdAt(media.getCreatedAt())
                                            .updatedAt(media.getUpdatedAt())
                                            .build())
                                    .collect(Collectors.toList());
                        }
                        return null;
                    }).map(src -> src, PropertyDetails::setMediaList);
                });
    }
}
