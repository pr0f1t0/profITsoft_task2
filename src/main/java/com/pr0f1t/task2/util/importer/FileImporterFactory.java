package com.pr0f1t.task2.util.importer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FileImporterFactory {

    private final Map<ImportType, FileImporterStrategy> fileImporterMap;

    public FileImporterFactory(List<FileImporterStrategy> fileImporterStrategies) {

        fileImporterMap = fileImporterStrategies.stream()
                .collect(Collectors.toMap(FileImporterStrategy::getImportType, Function.identity()));

    }

    public FileImporterStrategy getFileImporter(ImportType importType) {

        FileImporterStrategy fileImporterStrategy = fileImporterMap.get(importType);

        if (fileImporterStrategy == null) {
            throw new IllegalArgumentException("No such export type: " + importType);
        }

        return fileImporterStrategy;
    }

}
