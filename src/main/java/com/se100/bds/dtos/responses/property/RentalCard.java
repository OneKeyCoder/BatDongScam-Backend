package com.se100.bds.dtos.responses.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RentalCard {
    private BigDecimal totalArea;
    private BigDecimal monthlyRent;
}
