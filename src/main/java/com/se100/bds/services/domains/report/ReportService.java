package com.se100.bds.services.domains.report;

import com.se100.bds.dtos.responses.statisticreport.AgentPerformanceStats;
import com.se100.bds.dtos.responses.statisticreport.CustomerStats;
import com.se100.bds.dtos.responses.statisticreport.FinancialStats;
import com.se100.bds.dtos.responses.statisticreport.PropertyOwnerStats;

public interface ReportService {
    AgentPerformanceStats getAgentPerformanceStats(int year);
    CustomerStats getCustomerStats(int year);
    PropertyOwnerStats getPropertyOwnerStats(int year);
    FinancialStats getFinancialStats(int year);
}
