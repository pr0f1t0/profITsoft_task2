package com.pr0f1t.task2.service;

import com.pr0f1t.task2.dto.manufacturer.AddManufacturerDto;
import com.pr0f1t.task2.dto.manufacturer.ManufacturerResponseDto;

import java.util.List;
import java.util.UUID;

public interface ManufacturerService {
    List<ManufacturerResponseDto>  getAllManufacturers();
    ManufacturerResponseDto createManufacturer(AddManufacturerDto addManufacturerDto);
    ManufacturerResponseDto updateManufacturer(UUID manufacturerId, AddManufacturerDto addManufacturerDto);
    void deleteManufacturer(UUID id);
}
