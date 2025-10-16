package com.se100.bds.dtos.responses.location;

import com.se100.bds.dtos.responses.AbstractBaseDataResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CityResponse extends AbstractBaseDataResponse {
    private String cityName;
    private String description;
    private String imgUrl;
    private BigDecimal totalArea;
    private BigDecimal avgLandPrice;
    private Integer population;
    private Boolean isActive;
}

