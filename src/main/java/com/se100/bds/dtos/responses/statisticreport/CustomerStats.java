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
public class CustomerStats {
    Map<Integer, Integer> totalCustomers;
    Map<Integer, BigDecimal> totalSpending;
    Map<Integer, BigDecimal> avgSpendingPerCustomer;
    Map<Integer, String> tierDistribution;
}
