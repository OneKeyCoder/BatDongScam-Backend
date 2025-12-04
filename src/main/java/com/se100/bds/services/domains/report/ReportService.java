package com.se100.bds.services.domains.report;

import com.se100.bds.dtos.responses.statisticreport.*;

public interface ReportService {
    AgentPerformanceStats getAgentPerformanceStats(int year);
    CustomerStats getCustomerStats(int year);
    PropertyOwnerStats getPropertyOwnerStats(int year);
    FinancialStats getFinancialStats(int year);
    PropertyStats getPropertyStats(int year);
}
