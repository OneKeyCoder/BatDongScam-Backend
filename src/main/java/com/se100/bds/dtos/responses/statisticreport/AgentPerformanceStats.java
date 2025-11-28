package com.se100.bds.dtos.responses.statisticreport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AgentPerformanceStats {
    Map<Integer, Integer> totalAgents;
    Map<Integer, Double> agentRating;
    Map<Integer, Double> customerSatisfaction;
    Map<Integer, String> tierDistribution;
}
