package com.se100.bds.services.domains.ranking;

import com.se100.bds.models.schemas.ranking.*;
import com.se100.bds.utils.Constants;

import java.util.UUID;

public interface RankingService {
    String getTier(UUID userId, Constants.RoleEnum role, int month, int year);
    String getCurrentTier(UUID userId, Constants.RoleEnum role);
    IndividualSalesAgentPerformanceMonth getSaleAgentMonth(UUID agentId, int month, int year);
    IndividualSalesAgentPerformanceMonth getSaleAgentCurrentMonth(UUID agentId);
    IndividualSalesAgentPerformanceCareer getSaleAgentCareer(UUID agentId);
    IndividualCustomerPotentialMonth getCustomerMonth(UUID customerId, int month, int year);
    IndividualCustomerPotentialMonth getCustomerCurrentMonth(UUID customerId);
    IndividualCustomerPotentialAll getCustomerAll(UUID customerId);
    IndividualPropertyOwnerContributionMonth getPropertyOwnerMonth(UUID propertyOwnerId, int month, int year);
    IndividualPropertyOwnerContributionMonth getPropertyOwnerCurrentMonth(UUID propertyOwnerId);
    IndividualPropertyOwnerContributionAll getPropertyOwnerAll(UUID propertyOwnerId);

    // TODO: Action methods
}
