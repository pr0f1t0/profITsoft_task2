package com.pr0f1t.task2.controller;

import com.pr0f1t.task2.dto.ImportResultDto;
import com.pr0f1t.task2.dto.PageResponse;
import com.pr0f1t.task2.dto.laptop.AddLaptopDto;
import com.pr0f1t.task2.dto.laptop.LaptopResponseDto;
import com.pr0f1t.task2.dto.laptop.LaptopSearchRequest;
import com.pr0f1t.task2.dto.laptop.LaptopShortResponseDto;
import com.pr0f1t.task2.service.LaptopService;
import com.pr0f1t.task2.util.exporter.ExportType;
import com.pr0f1t.task2.util.importer.ImportType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping(path = "/api/laptop")
@RequiredArgsConstructor
public class LaptopController {

    private final LaptopService laptopService;

    @PostMapping
    public ResponseEntity<LaptopResponseDto> addLaptop(@RequestBody @Valid AddLaptopDto addLaptopDto){

        LaptopResponseDto addedLaptop = laptopService.addLaptop(addLaptopDto);

        return ResponseEntity.ok(addedLaptop);
    }

    @GetMapping("/{laptopId}")
    public ResponseEntity<LaptopResponseDto> getLaptopById(@PathVariable UUID laptopId){

        LaptopResponseDto laptop = laptopService.getLaptopById(laptopId);

        return ResponseEntity.ok(laptop);
    }

    @PutMapping("/{laptopId}")
    public ResponseEntity<LaptopResponseDto> updateLaptop(@RequestBody @Valid AddLaptopDto dto,
                                                          @PathVariable UUID laptopId){

        LaptopResponseDto updatedLaptop = laptopService.updateLaptop(laptopId, dto);

        return ResponseEntity.ok(updatedLaptop);
    }

    @DeleteMapping("/{laptopId}")
    public ResponseEntity<Void> removeLaptop(@PathVariable UUID laptopId){

        laptopService.removeLaptopById(laptopId);

        return ResponseEntity.noContent().build();

    }

    @PostMapping("/_list")
    public ResponseEntity<PageResponse<LaptopShortResponseDto>> getAllLaptops(@RequestBody
                                                                                  @Valid LaptopSearchRequest request){
        PageResponse<LaptopShortResponseDto> result = laptopService.getAllLaptops(request);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/_report")
    public ResponseEntity<Resource> csvReport(@RequestBody @Valid LaptopSearchRequest request){

        String filename = "searchReport.csv";

        ByteArrayInputStream result = laptopService.generateReport(request, ExportType.CSV);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(result));
    }

    @PostMapping("/upload")
    public ResponseEntity<ImportResultDto> importFromJson(@RequestParam("file")MultipartFile file) throws IOException {

        return ResponseEntity.ok(laptopService.importData(file, ImportType.JSON));
    }

}
