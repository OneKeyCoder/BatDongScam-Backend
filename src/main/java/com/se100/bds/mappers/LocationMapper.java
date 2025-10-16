package com.se100.bds.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper extends BaseMapper {
    @Autowired
    public LocationMapper(ModelMapper modelMapper) {
        super(modelMapper);
    }

    @Override
    protected void configureCustomMappings() {
        // No custom mappings needed for simple location entities
    }
}

