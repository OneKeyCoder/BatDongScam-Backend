package com.se100.bds.services.domains.ranking.scheduler;

import com.se100.bds.models.schemas.ranking.IndividualSalesAgentPerformanceCareer;
import com.se100.bds.models.schemas.ranking.IndividualSalesAgentPerformanceMonth;
import com.se100.bds.repositories.domains.mongo.ranking.IndividualSalesAgentPerformanceCareerRepository;
import com.se100.bds.repositories.domains.mongo.ranking.IndividualSalesAgentPerformanceMonthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SaleAgentRankingScheduler {
    private final IndividualSalesAgentPerformanceMonthRepository individualSalesAgentPerformanceMonthRepository;
    private final IndividualSalesAgentPerformanceCareerRepository individualSalesAgentPerformanceCareerRepository;

    // Run every day at 00:00 AM (midnight)
    @Scheduled(cron = "0 0 0 * * ?")
    private void calculateRanking() {
        calculateRankingMonth();
        calculateRankingAll();
        calculateRankingPosition();
    }

    private void calculateRankingMonth() {

        List<IndividualSalesAgentPerformanceMonth> agentPerformanceMonthList = individualSalesAgentPerformanceMonthRepository.findAll();
        for (IndividualSalesAgentPerformanceMonth agentPerformanceMonth : agentPerformanceMonthList) {
            updatePointMonth(agentPerformanceMonth);
        }

        individualSalesAgentPerformanceMonthRepository.saveAll(agentPerformanceMonthList);
    }

    private void calculateRankingAll() {
        List<IndividualSalesAgentPerformanceCareer> agentPerformanceCareerList = individualSalesAgentPerformanceCareerRepository.findAll();
        for (IndividualSalesAgentPerformanceCareer agentPerformanceCareer : agentPerformanceCareerList) {
            updatePointAll(agentPerformanceCareer);
        }

        individualSalesAgentPerformanceCareerRepository.saveAll(agentPerformanceCareerList);
    }

    private void updatePointMonth(IndividualSalesAgentPerformanceMonth individualSalesAgentPerformanceMonth) {

    }

    private void updatePointAll(IndividualSalesAgentPerformanceCareer individualSalesAgentPerformanceCareer) {

    }

    private void calculateRankingPosition() {
        int month = LocalDate.now().getMonthValue();
        int  year = LocalDate.now().getYear();
    }
}
