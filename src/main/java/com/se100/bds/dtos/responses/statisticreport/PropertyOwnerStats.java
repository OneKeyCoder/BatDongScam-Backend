package com.se100.bds.dtos.responses.statisticreport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PropertyOwnerStats {
    Map<Integer, Integer> totalOwners;
    Map<Integer, BigDecimal> totalContributionValue;
    Map<Integer, BigDecimal> avgContributionPerOwner;
    Map<Integer, String> tierDistribution;
}
