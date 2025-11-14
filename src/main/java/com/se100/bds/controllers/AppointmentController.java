package com.se100.bds.controllers;

import com.se100.bds.controllers.base.AbstractBaseController;
import com.se100.bds.dtos.responses.PageResponse;
import com.se100.bds.dtos.responses.appointment.ViewingCardDto;
import com.se100.bds.services.domains.appointment.AppointmentService;
import com.se100.bds.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.se100.bds.utils.Constants.SECURITY_SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointment")
@Tag(name = "008. Appointment/Viewing", description = "Appointment API")
@Slf4j
public class AppointmentController extends AbstractBaseController {
    private final AppointmentService appointmentService;

    @GetMapping("/viewing-cards")
    @Operation(
            summary = "Get my viewing cards",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<PageResponse<ViewingCardDto>> getMyViewingCards(
            @Parameter(description = "Page number (1-based)")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "desc") String sortType,
            @Parameter(description = "Field to sort by")
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Constants.AppointmentStatusEnum statusEnum,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        Pageable pageable = createPageable(page, limit, sortType, sortBy);
        Page<ViewingCardDto> viewingCardDtos = appointmentService.myViewingCards(pageable, statusEnum, day, month, year);
        return responseFactory.successPage(viewingCardDtos, "My viewings retrieved successfully");
    }
}
