package com.pr0f1t.task2.service.impl;

import com.pr0f1t.task2.dto.manufacturer.AddManufacturerDto;
import com.pr0f1t.task2.dto.manufacturer.ManufacturerResponseDto;
import com.pr0f1t.task2.entity.Manufacturer;
import com.pr0f1t.task2.exception.ManufacturerExistsException;
import com.pr0f1t.task2.exception.ManufacturerNotFoundException;
import com.pr0f1t.task2.mapper.ManufacturerMapper;
import com.pr0f1t.task2.repository.ManufacturerRepository;
import com.pr0f1t.task2.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private ManufacturerRepository manufacturerRepository;
    private ManufacturerMapper manufacturerMapper;

    @Override
    public List<ManufacturerResponseDto> getAllManufacturers() {

        List<Manufacturer> manufacturers = manufacturerRepository.findAll();

        return manufacturers.stream().map(manufacturerMapper::toResponseDto).toList();
    }

    @Override
    public ManufacturerResponseDto createManufacturer(AddManufacturerDto addManufacturerDto) {

        if(existsByName(addManufacturerDto.getName())){
            throw new ManufacturerExistsException("Manufacturer already exists");
        }

        Manufacturer manufacturer = Manufacturer.builder().name(addManufacturerDto.getName()).build();

        return manufacturerMapper.toResponseDto(manufacturerRepository.save(manufacturer));
    }

    @Override
    public ManufacturerResponseDto updateManufacturer(UUID manufacturerId, AddManufacturerDto addManufacturerDto) {

        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new ManufacturerNotFoundException("Manufacturer not found"));

        if(existsByName(addManufacturerDto.getName()) &&
                !manufacturer.getName().equals(addManufacturerDto.getName())){
            throw new ManufacturerExistsException("Manufacturer already exists");
        }

        manufacturer.setName(addManufacturerDto.getName());

        return manufacturerMapper.toResponseDto(manufacturerRepository.save(manufacturer));
    }

    @Override
    public void deleteManufacturer(UUID id) {

    }

    private boolean existsByName(String name) {
        return manufacturerRepository.findByName(name).isPresent();
    }
}
