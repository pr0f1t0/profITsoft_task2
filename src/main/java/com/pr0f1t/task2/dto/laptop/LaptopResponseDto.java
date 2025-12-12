package com.pr0f1t.task2.dto.laptop;

import com.pr0f1t.task2.dto.manufacturer.ManufacturerResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LaptopResponseDto {

    private UUID id;

    private String model;

    private Double price;

    private Integer ram;

    private Integer storage;

    private ManufacturerResponseDto manufacturer;

    private List<String> ports;

}
