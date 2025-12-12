package com.pr0f1t.task2.dto.laptop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LaptopShortResponseDto {

    private UUID id;

    private String model;

    private Double price;


}
