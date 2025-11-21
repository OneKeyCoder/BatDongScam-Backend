package com.se100.bds.services.domains.ranking.scheduler;

import com.se100.bds.models.schemas.ranking.IndividualCustomerPotentialAll;
import com.se100.bds.models.schemas.ranking.IndividualCustomerPotentialMonth;
import com.se100.bds.repositories.domains.mongo.ranking.IndividualCustomerPotentialAllRepository;
import com.se100.bds.repositories.domains.mongo.ranking.IndividualCustomerPotentialMonthRepository;
import com.se100.bds.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerRankingScheduler {
    private final IndividualCustomerPotentialMonthRepository individualCustomerPotentialMonthRepository;
    private final IndividualCustomerPotentialAllRepository individualCustomerPotentialAllRepository;

    // Run every day at 00:00 AM (midnight)
    @Scheduled(cron = "0 0 0 * * ?")
    private void calculateRanking() {
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
        int  year = LocalDate.now().getYear();
    }
}
