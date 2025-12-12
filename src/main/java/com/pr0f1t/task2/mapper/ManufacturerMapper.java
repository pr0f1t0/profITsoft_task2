package com.pr0f1t.task2.mapper;

import com.pr0f1t.task2.dto.manufacturer.ManufacturerResponseDto;
import com.pr0f1t.task2.entity.Manufacturer;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerMapper {

    public ManufacturerResponseDto toResponseDto(Manufacturer manufacturer) {
        return ManufacturerResponseDto.builder()
                .id(manufacturer.getId())
                .name(manufacturer.getName())
                .build();
    }

}
