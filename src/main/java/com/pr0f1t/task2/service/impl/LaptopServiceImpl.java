package com.pr0f1t.task2.service.impl;

import com.pr0f1t.task2.dto.ImportResultDto;
import com.pr0f1t.task2.dto.PageResponse;
import com.pr0f1t.task2.dto.laptop.*;
import com.pr0f1t.task2.exception.ImportException;
import com.pr0f1t.task2.util.importer.ParseResult;
import com.pr0f1t.task2.entity.Laptop;
import com.pr0f1t.task2.entity.Manufacturer;
import com.pr0f1t.task2.exception.LaptopNotFoundException;
import com.pr0f1t.task2.exception.ManufacturerNotFoundException;
import com.pr0f1t.task2.mapper.LaptopMapper;
import com.pr0f1t.task2.repository.LaptopRepository;
import com.pr0f1t.task2.repository.ManufacturerRepository;
import com.pr0f1t.task2.service.LaptopService;
import com.pr0f1t.task2.util.exporter.ExportType;
import com.pr0f1t.task2.util.exporter.FileExporterStrategy;
import com.pr0f1t.task2.util.exporter.FileExporterFactory;
import com.pr0f1t.task2.util.importer.FileImporterFactory;
import com.pr0f1t.task2.util.importer.FileImporterStrategy;
import com.pr0f1t.task2.util.importer.ImportType;
import jakarta.persistence.criteria.Predicate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class LaptopServiceImpl implements LaptopService {

    private final LaptopRepository laptopRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final LaptopMapper laptopMapper;
    private final FileExporterFactory fileExporterFactory;
    private final FileImporterFactory fileImporterFactory;

    @Override
    public LaptopResponseDto addLaptop(AddLaptopDto addLaptopDto) {

        Manufacturer manufacturer = manufacturerRepository.findById(addLaptopDto.getManufacturerId())
                .orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer not found"));

        Laptop laptop = Laptop.builder()
                .model(addLaptopDto.getModel())
                .price(addLaptopDto.getPrice())
                .ram(addLaptopDto.getRam())
                .storage(addLaptopDto.getStorage())
                .manufacturer(manufacturer)
                .ports(addLaptopDto.getPorts())
                .build();

        return laptopMapper.toResponseDto(laptopRepository.save(laptop));
    }

    @Override
    public LaptopResponseDto getLaptopById(UUID laptopId) {

        Laptop laptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new LaptopNotFoundException("Laptop not found"));

        return laptopMapper.toResponseDto(laptop);
    }

    @Override
    public LaptopResponseDto updateLaptop(UUID laptopId, AddLaptopDto addLaptopDto) {

        Laptop laptop = laptopRepository.findById(laptopId)
                .orElseThrow(() -> new LaptopNotFoundException("Laptop not found"));

        laptop.setModel(addLaptopDto.getModel());
        laptop.setPrice(addLaptopDto.getPrice());
        laptop.setRam(addLaptopDto.getRam());
        laptop.setStorage(addLaptopDto.getStorage());
        laptop.setPorts(addLaptopDto.getPorts());

        return laptopMapper.toResponseDto(laptopRepository.save(laptop));
    }

    @Override
    public PageResponse<LaptopShortResponseDto> getAllLaptops(LaptopSearchRequest laptopSearchRequest) {

        Pageable pageable = PageRequest.of(
                Math.max(0, laptopSearchRequest.getPage() - 1),
                laptopSearchRequest.getSize()
        );

        Specification<Laptop> specification = buildSpecification(laptopSearchRequest);

        Page<Laptop> pageResult = laptopRepository.findAll(specification, pageable);

        List<LaptopShortResponseDto> dtoList = pageResult.getContent().stream()
                .map(laptopMapper::toShortResponseDto)
                .toList();

        return new PageResponse<>(dtoList, pageResult.getTotalPages());
    }

    @Override
    public void removeLaptopById(UUID laptopId) {
        laptopRepository.deleteById(laptopId);
    }

    @Override
    public ByteArrayInputStream generateReport(LaptopSearchRequest laptopSearchRequest, ExportType exportType) {
        Specification<Laptop> specification = buildSpecification(laptopSearchRequest);

        List<LaptopShortResponseDto> filterResult = laptopRepository.findAll(specification).stream()
                .map(laptopMapper::toShortResponseDto)
                .toList();

        FileExporterStrategy exporter = fileExporterFactory.getFileExporter(exportType);

        return exporter.export(filterResult, LaptopShortResponseDto.class);
    }

    @Transactional
    @Override
    public ImportResultDto importData(MultipartFile file, ImportType exportType){
        FileImporterStrategy importer = fileImporterFactory.getFileImporter(ImportType.JSON);

        try{

            ParseResult<ImportLaptopDto> parseResult = importer.importData(file.getInputStream());

            for (ImportLaptopDto dto: parseResult.getResultList()){
                try{
                    saveLaptop(dto);
                }catch (Exception e){
                    parseResult.addFailure(e.getMessage());
                }
            }

            parseResult.getErrorList()
                    .forEach(log::warn);

            return new ImportResultDto(parseResult.getSuccessfulCount(), parseResult.getFailedCount());

        }catch (IOException e){

            throw new ImportException("Error reading JSON file");

        }

    }


    private void saveLaptop(ImportLaptopDto importLaptopDto) {

        Laptop laptop = Laptop.builder()
                .model(importLaptopDto.getModel())
                .price(importLaptopDto.getPrice())
                .ram(importLaptopDto.getRam())
                .storage(importLaptopDto.getStorage())
                .manufacturer(manufacturerRepository.findByName(importLaptopDto.getManufacturer())
                        .orElseGet(() -> {
                            Manufacturer m = Manufacturer.builder()
                                    .name(importLaptopDto.getManufacturer())
                                    .build();
                            return manufacturerRepository.save(m);
                        }))
                .ports(importLaptopDto.getPorts())
                .build();

        laptopRepository.save(laptop);

    }


    private Specification<Laptop> buildSpecification(LaptopSearchRequest laptopSearchRequest) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(laptopSearchRequest.getManufacturers() != null) {
                predicates.add(root.get("manufacturer").in(laptopSearchRequest.getManufacturers()));
            }

            if(laptopSearchRequest.getRam() != null) {
                predicates.add(cb.equal(root.get("ram"), laptopSearchRequest.getRam()));
            }

            if(laptopSearchRequest.getStorage() != null) {
                predicates.add(cb.equal(root.get("storage"), laptopSearchRequest.getStorage()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

}
