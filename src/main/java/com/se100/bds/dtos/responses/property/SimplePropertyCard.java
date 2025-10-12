package com.se100.bds.dtos.responses.property;

import com.se100.bds.dtos.responses.AbstractBaseDataResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SimplePropertyCard<T> extends AbstractBaseDataResponse {
    private String title;
    private String thumbnailUrl;
    private boolean isFavorite;
    private int numberOfImages;
    private String location;
    private String status;
    private T details;
}
