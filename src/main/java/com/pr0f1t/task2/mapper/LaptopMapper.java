package com.pr0f1t.task2.mapper;

import com.pr0f1t.task2.dto.laptop.LaptopResponseDto;
import com.pr0f1t.task2.dto.laptop.LaptopShortResponseDto;
import com.pr0f1t.task2.entity.Laptop;
import org.springframework.stereotype.Component;


@Component
public class LaptopMapper {

    private final ManufacturerMapper manufacturerMapper;

    public LaptopMapper(ManufacturerMapper manufacturerMapper) {
        this.manufacturerMapper = manufacturerMapper;
    }


    public LaptopResponseDto toResponseDto(Laptop laptop) {
        return LaptopResponseDto.builder()
                .id(laptop.getId())
                .model(laptop.getModel())
                .price(laptop.getPrice())
                .ram(laptop.getRam())
                .storage(laptop.getStorage())
                .manufacturer(manufacturerMapper.toResponseDto(laptop.getManufacturer()))
                .build();
    }

    public LaptopShortResponseDto toShortResponseDto(Laptop laptop) {

        return LaptopShortResponseDto.builder()
                .id(laptop.getId())
                .model(laptop.getModel())
                .price(laptop.getPrice())
                .build();

    }


}
