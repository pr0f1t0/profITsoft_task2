package com.pr0f1t.task2.dto.manufacturer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddManufacturerDto {

    @NotBlank(message = "Name must not be empty")
    private String name;

}
