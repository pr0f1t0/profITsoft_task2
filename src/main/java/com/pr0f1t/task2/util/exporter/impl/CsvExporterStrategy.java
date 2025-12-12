package com.pr0f1t.task2.util.exporter.impl;

import com.pr0f1t.task2.util.exporter.ExportType;
import com.pr0f1t.task2.util.exporter.FileExporterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporterStrategy implements FileExporterStrategy {
    private static final Logger log = LoggerFactory.getLogger(CsvExporterStrategy.class);

    @Override
    public <T> ByteArrayInputStream export(List<T> data, Class<T> type) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try{
            Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withQuotechar(com.opencsv.CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(com.opencsv.CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(true)
                    .build();

            beanToCsv.write(data);
            writer.flush();

        }catch(CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException ex){
            log.error("Error while writing to csv", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ExportType getExportType() {
        return ExportType.CSV;
    }
}
