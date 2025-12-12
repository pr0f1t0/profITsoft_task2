package com.pr0f1t.task2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportResultDto {

    private int successfulCount;

    private int failedCount;

}
