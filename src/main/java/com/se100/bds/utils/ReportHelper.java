package com.se100.bds.utils;

import com.se100.bds.models.schemas.report.RankedItem;
import com.se100.bds.models.schemas.report.RankedRevenueItem;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper utility for converting Map to sorted List for reports
 */
public class ReportHelper {

    /**
     * Convert Map<UUID, Integer> to List<RankedItem> sorted by count DESC
     * Used for PropertyStatisticsReport
     */
    public static List<RankedItem> convertToSortedRankedList(Map<UUID, Integer> countMap) {
        if (countMap == null || countMap.isEmpty()) {
            return new ArrayList<>();
        }

        return countMap.entrySet().stream()
            .map(entry -> RankedItem.builder()
                .id(entry.getKey())
                .count(entry.getValue())
                .build())
            .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount())) // Sort DESC by count
            .collect(Collectors.toList());
    }

    /**
     * Convert Map<UUID, BigDecimal> to List<RankedRevenueItem> sorted by revenue DESC
     * Used for FinancialReport
     */
    public static List<RankedRevenueItem> convertToSortedRevenueList(Map<UUID, BigDecimal> revenueMap) {
        if (revenueMap == null || revenueMap.isEmpty()) {
            return new ArrayList<>();
        }

        return revenueMap.entrySet().stream()
            .map(entry -> RankedRevenueItem.builder()
                .id(entry.getKey())
                .revenue(entry.getValue())
                .build())
            .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue())) // Sort DESC by revenue
            .collect(Collectors.toList());
    }
}

