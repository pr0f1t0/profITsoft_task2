package com.pr0f1t.task2.controller;

import com.pr0f1t.task2.dto.manufacturer.AddManufacturerDto;
import com.pr0f1t.task2.dto.manufacturer.ManufacturerResponseDto;
import com.pr0f1t.task2.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/manufacturer")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping
    public ResponseEntity<List<ManufacturerResponseDto>> getAllManufacturers(){

        List<ManufacturerResponseDto> result = manufacturerService.getAllManufacturers();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ManufacturerResponseDto> addManufacturer(@RequestBody @Valid AddManufacturerDto dto){

        ManufacturerResponseDto addedManufacturer = manufacturerService.addManufacturer(dto);

        return ResponseEntity.ok(addedManufacturer);
    }

    @PutMapping("/{manufacturerId}")
    public ResponseEntity<ManufacturerResponseDto> updateManufacturer(@RequestBody @Valid AddManufacturerDto dto,
                                                                      @PathVariable UUID manufacturerId){

        ManufacturerResponseDto updatedManufacturer = manufacturerService.updateManufacturer(manufacturerId, dto);

        return ResponseEntity.ok(updatedManufacturer);
    }

    @DeleteMapping("/{manufacturerId}")
    public ResponseEntity<Void> removeManufacturer(@PathVariable UUID manufacturerId){

        manufacturerService.removeManufacturerById(manufacturerId);

        return ResponseEntity.noContent().build();
    }



}
