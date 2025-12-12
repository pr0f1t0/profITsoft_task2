package com.pr0f1t.task2.dto.laptop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportLaptopDto {

    private String model;

    private Double price;

    private Integer ram;

    private Integer storage;

    private String manufacturer;

    private List<String> ports;

}
