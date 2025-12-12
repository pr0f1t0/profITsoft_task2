package com.pr0f1t.task2.util.importer.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.pr0f1t.task2.util.importer.ParseResult;
import com.pr0f1t.task2.dto.laptop.ImportLaptopDto;
import com.pr0f1t.task2.util.importer.FileImporterStrategy;
import com.pr0f1t.task2.util.importer.ImportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class JsonImporterStrategy implements FileImporterStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public ParseResult<ImportLaptopDto> importData(InputStream inputStream) {

        ParseResult<ImportLaptopDto> result = new ParseResult<>();
        JsonFactory jsonFactory = objectMapper.getFactory();

        try(JsonParser parser = jsonFactory.createParser(inputStream)){

            if(parser.nextToken() != JsonToken.START_ARRAY){
                result.addFailure("File content is not a JSON array");
                return result;
            }

            while(parser.nextToken() != JsonToken.END_ARRAY){
                try{
                    ImportLaptopDto dto = objectMapper.readValue(parser, ImportLaptopDto.class);

                    if (!isValidDto(dto)) {
                        result.addFailure("JSON object contains invalid data");
                        continue;
                    }

                    result.addSuccess(dto);
                }catch (Exception e){
                    result.addFailure("Error parsing item: " + e.getMessage());
                }
            }


        }catch (IOException e){
            result.addFailure("IOException: " + e.getMessage());
        }

        return null;
    }

    private boolean isValidDto(ImportLaptopDto dto){

        boolean isValidRam = dto.getRam() != null && dto.getRam() >= 1;
        boolean isValidStorage = dto.getStorage() != null && dto.getStorage() >= 64;

        return dto.getModel() != null && dto.getManufacturer() != null && isValidRam && isValidStorage;
    }

    @Override
    public ImportType getImportType() {
        return ImportType.JSON;
    }
}
