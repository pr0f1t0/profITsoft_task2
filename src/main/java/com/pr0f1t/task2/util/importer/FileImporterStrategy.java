package com.pr0f1t.task2.util.importer;

import com.pr0f1t.task2.dto.laptop.ImportLaptopDto;

import java.io.InputStream;

public interface FileImporterStrategy {

    ParseResult<ImportLaptopDto> importData(InputStream inputStream);

    ImportType getImportType();

}
