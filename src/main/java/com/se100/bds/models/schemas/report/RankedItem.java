package com.se100.bds.models.schemas.report;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankedItem {
    private UUID id;
    private Integer count;
}

