package com.se100.bds.models.schemas.report;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "financial_reports")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReport extends AbstractBaseMongoReport {
    @Field("total_revenue_current_month")
    private BigDecimal totalRevenueCurrentMonth;

    @Field("total_revenue")
    private BigDecimal totalRevenue;

    @Field("total_service_fees_current_month")
    private BigDecimal totalServiceFeesCurrentMonth;

    @Field("contract_count_current_month")
    private BigDecimal contractCountCurrentMonth;

    @Field("contract_count")
    private BigDecimal contractCount;

    @Field("tax")
    private BigDecimal tax;

    @Field("net_profit")
    private BigDecimal netProfit;

    @Field("total_rates")
    private BigDecimal totalRates;

    @Field("avg_rating")
    private BigDecimal avgRating;

    @Field("total_rates_current_month")
    private BigDecimal totalRatesCurrentMonth;

    @Field("avg_rating_current_month")
    private BigDecimal avgRatingCurrentMonth;

    @Field("revenue_cities")
    private List<RankedRevenueItem> revenueCities;

    @Field("revenue_cities_current_month")
    private List<RankedRevenueItem> revenueCitiesCurrentMonth;

    @Field("revenue_districts")
    private List<RankedRevenueItem> revenueDistricts;

    @Field("revenue_districts_current_month")
    private List<RankedRevenueItem> revenueDistrictsCurrentMonth;

    @Field("revenue_wards")
    private List<RankedRevenueItem> revenueWards;

    @Field("revenue_wards_current_month")
    private List<RankedRevenueItem> revenueWardsCurrentMonth;

    @Field("revenue_property_types")
    private List<RankedRevenueItem> revenuePropertyTypes;

    @Field("revenue_property_types_current_month")
    private List<RankedRevenueItem> revenuePropertyTypesCurrentMonth;

    @Field("revenue_sales_agents")
    private List<RankedRevenueItem> revenueSalesAgents;

    @Field("revenue_sales_agents_current_month")
    private List<RankedRevenueItem> revenueSalesAgentsCurrentMonth;

    @Field("sale_agents_salary_month")
    private List<RankedRevenueItem> saleAgentsSalaryMonth;

    @Field("sale_agents_salary_career")
    private List<RankedRevenueItem> saleAgentsSalaryCareer;
}
