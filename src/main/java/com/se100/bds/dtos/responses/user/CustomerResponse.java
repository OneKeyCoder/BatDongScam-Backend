package com.se100.bds.dtos.responses.user;

import com.se100.bds.utils.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.tomcat.util.bcel.Const;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CustomerResponse {
    private BigDecimal currentMonthSpending;
    private BigDecimal totalSpending;
    private int currentMonthPurchases;
    private int totalPurchases;
    private int currentMonthRentals;
    private int totalRentals;
    private int currentMonthSearches;
    private int currentMonthViewings;
    private Constants.CustomerTierEnum customerTier;
    private int leadScore;
}