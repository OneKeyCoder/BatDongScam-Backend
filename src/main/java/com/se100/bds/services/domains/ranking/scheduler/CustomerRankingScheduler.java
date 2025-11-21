package com.se100.bds.services.domains.ranking.scheduler;

import com.se100.bds.models.entities.user.User;
import com.se100.bds.models.schemas.ranking.IndividualCustomerPotentialAll;
import com.se100.bds.models.schemas.ranking.IndividualCustomerPotentialMonth;
import com.se100.bds.repositories.domains.mongo.ranking.IndividualCustomerPotentialAllRepository;
import com.se100.bds.repositories.domains.mongo.ranking.IndividualCustomerPotentialMonthRepository;
import com.se100.bds.services.domains.user.UserService;
import com.se100.bds.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerRankingScheduler {
    private final IndividualCustomerPotentialMonthRepository individualCustomerPotentialMonthRepository;
    private final IndividualCustomerPotentialAllRepository individualCustomerPotentialAllRepository;
    private final UserService userService;

    // Run every day at 00:00 AM (midnight)
    @Scheduled(cron = "0 0 0 * * ?")
    private void calculateRanking() {
        createIfNotExist();

        calculateRankingMonth();
        calculateRankingAll();
        calculateRankingPosition();
    }
    
    private void calculateRankingMonth() {

        List<IndividualCustomerPotentialMonth> customerPerformanceMonthList = individualCustomerPotentialMonthRepository.findAll();
        for (IndividualCustomerPotentialMonth customerPerformanceMonth : customerPerformanceMonthList) {
            updatePointMonth(customerPerformanceMonth);
        }

        individualCustomerPotentialMonthRepository.saveAll(customerPerformanceMonthList);
    }

    private void calculateRankingAll() {
        List<IndividualCustomerPotentialAll> customerPerformanceCareerList = individualCustomerPotentialAllRepository.findAll();
        for (IndividualCustomerPotentialAll customerPerformanceCareer : customerPerformanceCareerList) {
            updatePointAll(customerPerformanceCareer);
        }

        individualCustomerPotentialAllRepository.saveAll(customerPerformanceCareerList);
    }
    
    private void updatePointMonth(IndividualCustomerPotentialMonth individualCustomerPotentialMonth) {

    }
    
    private void updatePointAll(IndividualCustomerPotentialAll individualCustomerPotentialAll) {

    }
    
    private void calculateRankingPosition() {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        // Calculate ranking for current month
        try {
            List<IndividualCustomerPotentialMonth> customerPotentialMonthList = individualCustomerPotentialMonthRepository.findAll().stream()
                    .filter(m -> m.getMonth().equals(month) && m.getYear().equals(year))
                    .collect(Collectors.toList());

            // Sort by leadScore descending
            customerPotentialMonthList.sort((c1, c2) -> c2.getLeadScore().compareTo(c1.getLeadScore()));

            // Update ranking with handling for same points
            int ranking = 1;
            Integer previousPoint = null;
            int currentPosition = 1;

            for (IndividualCustomerPotentialMonth customerPotential : customerPotentialMonthList) {
                if (previousPoint != null && !previousPoint.equals(customerPotential.getLeadScore())) {
                    // Different point, update ranking to current position
                    ranking = currentPosition;
                }
                customerPotential.setLeadPosition(ranking);
                previousPoint = customerPotential.getLeadScore();
                currentPosition++;
            }

            // Save all
            individualCustomerPotentialMonthRepository.saveAll(customerPotentialMonthList);
        } catch (Exception e) {
            log.error("calculateRankingPosition for month - {}", e.getMessage());
        }

        // Calculate ranking for all time
        try {
            List<IndividualCustomerPotentialAll> customerPotentialAllList = individualCustomerPotentialAllRepository.findAll();

            // Sort by leadScore descending
            customerPotentialAllList.sort((c1, c2) -> c2.getLeadScore().compareTo(c1.getLeadScore()));

            // Update ranking with handling for same points
            int ranking = 1;
            Integer previousPoint = null;
            int currentPosition = 1;

            for (IndividualCustomerPotentialAll customerPotential : customerPotentialAllList) {
                if (previousPoint != null && !previousPoint.equals(customerPotential.getLeadScore())) {
                    // Different point, update ranking to current position
                    ranking = currentPosition;
                }
                customerPotential.setLeadPosition(ranking);
                previousPoint = customerPotential.getLeadScore();
                currentPosition++;
            }

            // Save all
            individualCustomerPotentialAllRepository.saveAll(customerPotentialAllList);
        } catch (Exception e) {
            log.error("calculateRankingPosition for all - {}", e.getMessage());
        }
    }

    private void createIfNotExist() {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        List<User> availableCustomers = userService.findAllByRoleAndStillAvailable(Constants.RoleEnum.CUSTOMER);


        for (User availableCustomer : availableCustomers) {

            // Check if data exists for all history tracking
            if (individualCustomerPotentialAllRepository.findByCustomerId(
                    availableCustomer.getId()
            ) == null) {
                individualCustomerPotentialAllRepository.save(
                    IndividualCustomerPotentialAll.builder()
                            .customerId(availableCustomer.getId())
                            .leadScore(0)
                            .leadPosition(0)
                            .viewingsRequested(0)
                            .viewingsAttended(0)
                            .spending(BigDecimal.ZERO)
                            .totalPurchases(0)
                            .totalRentals(0)
                            .totalContractsSigned(0)
                            .build()
                );
            }

            // Check if data exists for current month tracking
            if (individualCustomerPotentialMonthRepository.findByCustomerIdAndMonthAndYear(
                    availableCustomer.getId(),
                    month, year
            ) == null) {
                individualCustomerPotentialMonthRepository.save(
                        IndividualCustomerPotentialMonth.builder()
                                .customerId(availableCustomer.getId())
                                .month(month)
                                .year(year)
                                .leadScore(0)
                                .customerTier(Constants.CustomerTierEnum.BRONZE)
                                .leadPosition(0)
                                .monthViewingsRequested(0)
                                .monthViewingAttended(0)
                                .monthSpending(BigDecimal.ZERO)
                                .monthPurchases(0)
                                .monthRentals(0)
                                .build()
                );
            }
        }
    }
}
