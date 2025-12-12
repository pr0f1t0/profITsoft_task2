package com.pr0f1t.task2.dto.laptop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddLaptopDto {

    @NotBlank(message = "Model cannot be empty")
    private String model;

    @NotNull
    @Range(min = 1, message = "Price cannot be smaller than zero")
    private Double price;

    @NotNull
    @Range(min = 1, message = "RAM cannot be smaller than one")
    private Integer ram;

    @NotNull
    @Range(min = 64, message = "Storage cannot be smaller than 64")
    private Integer storage;

    private UUID manufacturerId;

    private List<String> ports;

}

