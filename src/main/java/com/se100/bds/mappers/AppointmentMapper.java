package com.se100.bds.mappers;

import com.se100.bds.dtos.responses.appointment.ViewingDetails;
import com.se100.bds.dtos.responses.user.simple.PropertyOwnerSimpleCard;
import com.se100.bds.dtos.responses.user.simple.SalesAgentSimpleCard;
import com.se100.bds.models.entities.appointment.Appointment;
import com.se100.bds.models.entities.user.PropertyOwner;
import com.se100.bds.models.entities.user.SaleAgent;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper extends BaseMapper {
    @Autowired
    public AppointmentMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    protected void configureCustomMappings() {
        // Configure mapping from Appointment to ViewingDetails
        modelMapper.typeMap(Appointment.class, ViewingDetails.class)
                .addMappings(mapper -> {
                    // Map property fields to ViewingDetails
                    mapper.map(src -> src.getProperty().getTitle(), ViewingDetails::setTitle);
                    mapper.map(src -> src.getProperty().getPriceAmount(), ViewingDetails::setPriceAmount);
                    mapper.map(src -> src.getProperty().getArea(), ViewingDetails::setArea);
                    mapper.map(src -> src.getProperty().getDescription(), ViewingDetails::setDescription);
                    mapper.map(src -> src.getProperty().getRooms(), ViewingDetails::setRooms);
                    mapper.map(src -> src.getProperty().getBathrooms(), ViewingDetails::setBathRooms);
                    mapper.map(src -> src.getProperty().getBedrooms(), ViewingDetails::setBedRooms);
                    mapper.map(src -> src.getProperty().getFloors(), ViewingDetails::setFloors);
                    mapper.map(src -> src.getProperty().getHouseOrientation(), ViewingDetails::setHouseOrientation);
                    mapper.map(src -> src.getProperty().getBalconyOrientation(), ViewingDetails::setBalconyOrientation);
                });
    }

    /**
     * Build PropertyOwnerSimpleCard from PropertyOwner entity
     */
    public PropertyOwnerSimpleCard buildOwnerCard(PropertyOwner owner, String tier) {
        return PropertyOwnerSimpleCard.builder()
                .id(owner.getId())
                .firstName(owner.getUser().getFirstName())
                .lastName(owner.getUser().getLastName())
                .phoneNumber(owner.getUser().getPhoneNumber())
                .zaloContact(owner.getUser().getZaloContact())
                .email(owner.getUser().getEmail())
                .tier(tier)
                .build();
    }

    /**
     * Build SalesAgentSimpleCard from SaleAgent entity
     */
    public SalesAgentSimpleCard buildAgentCard(SaleAgent agent, String tier, double rating, int totalRates) {
        return SalesAgentSimpleCard.builder()
                .id(agent.getId())
                .firstName(agent.getUser().getFirstName())
                .lastName(agent.getUser().getLastName())
                .phoneNumber(agent.getUser().getPhoneNumber())
                .zaloContact(agent.getUser().getZaloContact())
                .tier(tier)
                .rating(rating)
                .totalRates(totalRates)
                .build();
    }
}
