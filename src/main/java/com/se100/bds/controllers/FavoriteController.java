package com.se100.bds.controllers;

import com.se100.bds.controllers.base.AbstractBaseController;
import com.se100.bds.dtos.responses.PageResponse;
import com.se100.bds.dtos.responses.SingleResponse;
import com.se100.bds.dtos.responses.error.ErrorResponse;
import com.se100.bds.dtos.responses.property.SimplePropertyCard;
import com.se100.bds.mappers.PropertyMapper;
import com.se100.bds.repositories.domains.mongo.customer.CustomerFavoritePropertyRepository;
import com.se100.bds.models.schemas.customer.AbstractCustomerPreferenceMongoSchema;
import com.se100.bds.services.domains.customer.CustomerFavoriteService;
import com.se100.bds.services.domains.property.PropertyService;
import com.se100.bds.services.domains.user.UserService;
import com.se100.bds.services.dtos.results.PropertyCard;
import com.se100.bds.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.se100.bds.utils.Constants.SECURITY_SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
@Tag(name = "004. Favorite", description = "Favorite API")
@Slf4j
public class FavoriteController extends AbstractBaseController {
    private final CustomerFavoriteService customerFavoriteService;
    private final CustomerFavoritePropertyRepository customerFavoritePropertyRepository;
    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;
    private final UserService userService;

    @PostMapping("/like")
    @Operation(
            summary = "Toggle like/unlike for a resource",
            description = "Add or remove a favorite/preference for properties, cities, districts, wards, or property types. Returns true if liked, false if unliked.",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - returns true if liked, false if unliked",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SingleResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid like type or ID",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user must be logged in",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<SingleResponse<Boolean>> like(
            @Parameter(description = "ID of the resource to like/unlike", required = true)
            @RequestParam UUID id,

            @Parameter(
                    description = "Type of resource: PROPERTY, CITY, DISTRICT, WARD, or PROPERTY_TYPE",
                    required = true,
                    example = "PROPERTY"
            )
            @RequestParam Constants.LikeTypeEnum likeType
    ) {
        log.info("Like request received - ID: {}, Type: {}", id, likeType);

        boolean isLiked = customerFavoriteService.like(id, likeType);

        String message = isLiked
                ? String.format("%s added to favorites", likeType.name().toLowerCase())
                : String.format("%s removed from favorites", likeType.name().toLowerCase());

        return responseFactory.successSingle(isLiked, message);
    }

    @GetMapping("/properties/cards")
    @Operation(
            summary = "Get my favorite properties as property cards",
            description = "Returns the current user's favorite properties in the same card DTO used by /public/properties/cards",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<PageResponse<SimplePropertyCard>> myFavoritePropertyCards(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "15") int limit
    ) {
        Pageable pageable = createPageable(page, limit, "desc", "createdAt");

        UUID customerId = userService.getUserId();
        List<UUID> favoritePropertyIds = customerFavoritePropertyRepository.findByCustomerId(customerId)
                .stream()
                .map(AbstractCustomerPreferenceMongoSchema::getRefId)
                .toList();

        if (favoritePropertyIds.isEmpty()) {
            Page<SimplePropertyCard> empty = Page.empty(pageable);
            return responseFactory.successPage(empty, "Favorite property cards retrieved successfully");
        }

        Page<PropertyCard> favoriteCards = propertyService.getFavoritePropertyCards(favoritePropertyIds, pageable);
        Page<SimplePropertyCard> responsePage = propertyMapper.mapToPage(favoriteCards, SimplePropertyCard.class);

        // make sure favorite flag is always true
        responsePage.forEach(c -> c.setFavorite(true));

        return responseFactory.successPage(responsePage, "Favorite property cards retrieved successfully");
    }
}
