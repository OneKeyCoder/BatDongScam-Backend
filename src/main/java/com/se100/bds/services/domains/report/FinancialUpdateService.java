package com.se100.bds.services.domains.report;

import java.math.BigDecimal;
import java.util.UUID;

public interface FinancialUpdateService {
    void transaction(UUID propertyId, BigDecimal value, int month, int year);
}
