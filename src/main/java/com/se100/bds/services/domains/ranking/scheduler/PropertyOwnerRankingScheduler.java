package com.se100.bds.services.domains.ranking.scheduler;

import com.se100.bds.models.schemas.ranking.IndividualPropertyOwnerContributionAll;
import com.se100.bds.models.schemas.ranking.IndividualPropertyOwnerContributionMonth;
import com.se100.bds.repositories.domains.mongo.ranking.IndividualPropertyOwnerContributionAllRepository;
import com.se100.bds.repositories.domains.mongo.ranking.IndividualPropertyOwnerContributionMonthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PropertyOwnerRankingScheduler {
    private final IndividualPropertyOwnerContributionMonthRepository individualPropertyOwnerContributionMonthRepository;
    private final IndividualPropertyOwnerContributionAllRepository individualPropertyOwnerContributionAllRepository;

    // Run every day at 00:00 AM (midnight)
    @Scheduled(cron = "0 0 0 * * ?")
    private void calculateRanking() {
        calculateRankingMonth();
        calculateRankingAll();
        calculateRankingPosition();
    }

    private void calculateRankingMonth() {

        List<IndividualPropertyOwnerContributionMonth> propertyOwnerContributionMonthList = individualPropertyOwnerContributionMonthRepository.findAll();
        for (IndividualPropertyOwnerContributionMonth propertyOwnerContributionMonth : propertyOwnerContributionMonthList) {
            updatePointMonth(propertyOwnerContributionMonth);
        }

        individualPropertyOwnerContributionMonthRepository.saveAll(propertyOwnerContributionMonthList);
    }

    private void calculateRankingAll() {
        List<IndividualPropertyOwnerContributionAll> propertyOwnerContributionAllList = individualPropertyOwnerContributionAllRepository.findAll();
        for (IndividualPropertyOwnerContributionAll propertyOwnerContributionAll : propertyOwnerContributionAllList) {
            updatePointAll(propertyOwnerContributionAll);
        }

        individualPropertyOwnerContributionAllRepository.saveAll(propertyOwnerContributionAllList);
    }

    private void updatePointMonth(IndividualPropertyOwnerContributionMonth individualPropertyOwnerContributionMonth) {

    }

    private void updatePointAll(IndividualPropertyOwnerContributionAll individualPropertyOwnerContributionAll) {

    }

    private void calculateRankingPosition() {
        int month = LocalDate.now().getMonthValue();
        int  year = LocalDate.now().getYear();
    }
}
