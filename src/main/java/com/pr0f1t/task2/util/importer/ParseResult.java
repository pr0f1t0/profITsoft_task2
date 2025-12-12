package com.pr0f1t.task2.util.importer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParseResult<T> {

    private final List<T> resultList = new ArrayList<>();

    private final List<String> errorList = new ArrayList<>();

    private int successfulCount;

    private int failedCount;

    public void addSuccess(T item) {
        resultList.add(item);
        successfulCount++;
    }

    public void addFailure(String errorMessage) {
        errorList.add(errorMessage);
        failedCount++;
    }
}