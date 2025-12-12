package com.pr0f1t.task2.util.exporter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FileExporterFactory {

    private final Map<ExportType, FileExporterStrategy> fileExporterMap;

    public FileExporterFactory(List<FileExporterStrategy> fileExporterStrategies) {

        fileExporterMap = fileExporterStrategies.stream()
                .collect(Collectors.toMap(FileExporterStrategy::getExportType, Function.identity()));

    }

    public FileExporterStrategy getFileExporter(ExportType exportType) {

        FileExporterStrategy fileExporterStrategy = fileExporterMap.get(exportType);

        if (fileExporterStrategy == null) {
            throw new IllegalArgumentException("No such export type: " + exportType);
        }

        return fileExporterStrategy;
    }

}
