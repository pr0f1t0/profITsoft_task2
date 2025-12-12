package com.pr0f1t.task2.service;

import com.pr0f1t.task2.dto.ImportResultDto;
import com.pr0f1t.task2.dto.PageResponse;
import com.pr0f1t.task2.dto.laptop.AddLaptopDto;
import com.pr0f1t.task2.dto.laptop.LaptopSearchRequest;
import com.pr0f1t.task2.dto.laptop.LaptopResponseDto;
import com.pr0f1t.task2.dto.laptop.LaptopShortResponseDto;
import com.pr0f1t.task2.util.exporter.ExportType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;


public interface LaptopService {

    LaptopResponseDto addLaptop(AddLaptopDto  addLaptopDto);
    LaptopResponseDto getLaptopById(UUID laptopId);
    LaptopResponseDto updateLaptop(UUID laptopId, AddLaptopDto addLaptopDto);
    PageResponse<LaptopShortResponseDto> getAllLaptops(LaptopSearchRequest laptopSearchRequest);
    ByteArrayInputStream generateReport(LaptopSearchRequest laptopSearchRequest, ExportType exportType);
    ImportResultDto importData(InputStream stream, ExportType exportType);
    void removeLaptopById(String laptopId);

}
