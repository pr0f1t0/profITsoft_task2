package com.pr0f1t.task2.dto.laptop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaptopSearchRequest {

    private List<UUID> manufacturers;

    @Range(min = 1, message = "RAM size cannot be smaller than one")
    private Integer ram;

    @Range(min = 64, message = "Storage size cannot be smaller than 64")
    private Integer storage;

    @Range(min = 1, message = "Page number cannot be smaller than one")
    private Integer page = 1;

    @Range(min = 1, message = "Page number number cannot be smaller than one")
    private Integer size = 20;

}
