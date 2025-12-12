package com.pr0f1t.task2.util.exporter;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface FileExporterStrategy {

    <T>ByteArrayInputStream export(List<T> data, Class<T> type);

    ExportType getExportType();
}
