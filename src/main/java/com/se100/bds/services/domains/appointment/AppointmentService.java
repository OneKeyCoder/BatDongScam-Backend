package com.se100.bds.services.domains.appointment;

import com.se100.bds.dtos.responses.appointment.ViewingCardDto;
import com.se100.bds.utils.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    Page<ViewingCardDto> myViewingCards(
            Pageable pageable,
            Constants.AppointmentStatusEnum statusEnum,
            Integer day, Integer month, Integer year
    );
}
