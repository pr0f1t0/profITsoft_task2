package com.pr0f1t.task2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pr0f1t.task2.controller.LaptopController;
import com.pr0f1t.task2.dto.ImportResultDto;
import com.pr0f1t.task2.dto.PageResponse;
import com.pr0f1t.task2.dto.laptop.AddLaptopDto;
import com.pr0f1t.task2.dto.laptop.LaptopResponseDto;
import com.pr0f1t.task2.dto.laptop.LaptopSearchRequest;
import com.pr0f1t.task2.dto.laptop.LaptopShortResponseDto;
import com.pr0f1t.task2.dto.manufacturer.ManufacturerResponseDto;
import com.pr0f1t.task2.service.LaptopService;
import com.pr0f1t.task2.util.exporter.ExportType;
import com.pr0f1t.task2.util.importer.ImportType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Spring Boot 3.4+
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LaptopController.class)
public class LaptopControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LaptopService laptopService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void csvReportShouldReturnCsvWithLaptopShortResponseData() throws Exception {

        LaptopSearchRequest request = new LaptopSearchRequest();
        request.setRam(16);
        request.setStorage(512);


        UUID laptopId1 = UUID.randomUUID();
        UUID laptopId2 = UUID.randomUUID();

        String expectedCsvContent = """
                id,model,price
                %s,MacBook Pro 16,2499.99
                %s,Dell XPS 15,1899.50
                """.formatted(laptopId1, laptopId2);

        ByteArrayInputStream reportStream = new ByteArrayInputStream(
                expectedCsvContent.getBytes(StandardCharsets.UTF_8)
        );

        when(laptopService.generateReport(any(LaptopSearchRequest.class), eq(ExportType.CSV)))
                .thenReturn(reportStream);

        mockMvc.perform(post("/api/laptops/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("filename=\"searchReport.csv\"")))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(expectedCsvContent));
    }

    @Test
    void csvReportShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        LaptopSearchRequest invalidRequest = new LaptopSearchRequest();
        invalidRequest.setRam(-5);

        mockMvc.perform(post("/api/laptops/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldAddLaptopAndReturnResponse_WhenRequestIsValid() throws Exception {
        UUID manufacturerId = UUID.randomUUID();

        AddLaptopDto request = AddLaptopDto.builder()
                .model("MacBook Pro")
                .price(2500.00)
                .ram(32)
                .storage(1024)
                .manufacturerId(manufacturerId)
                .ports(List.of("USB-C", "HDMI"))
                .build();

        LaptopResponseDto response = LaptopResponseDto.builder()
                .id(UUID.randomUUID())
                .model(request.getModel())
                .price(request.getPrice())
                .ram(request.getRam())
                .storage(request.getStorage())
                .manufacturer(ManufacturerResponseDto.builder()
                        .id(manufacturerId)
                        .name("Apple")
                        .build())
                .ports(request.getPorts())
                .build();

        when(laptopService.addLaptop(any(AddLaptopDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/laptops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.model").value("MacBook Pro"))
                .andExpect(jsonPath("$.manufacturer.name").value("Apple"))
                .andExpect(jsonPath("$.ports[0]").value("USB-C"));
    }

    @Test
    void addShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        AddLaptopDto invalidRequest = AddLaptopDto.builder()
                .model("")
                .price(-100.00)
                .ram(0)
                .storage(32)
                .manufacturerId(null)
                .ports(null)
                .build();

        mockMvc.perform(post("/api/laptops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdShouldReturnLaptop_WhenIdExists() throws Exception {
        UUID laptopId = UUID.randomUUID();
        LaptopResponseDto response = LaptopResponseDto.builder()
                .id(laptopId)
                .model("Dell XPS 15")
                .price(1800.00)
                .ram(16)
                .storage(512)
                .manufacturer(ManufacturerResponseDto.builder()
                        .id(UUID.randomUUID())
                        .name("Dell")
                        .build())
                .ports(List.of("Thunderbolt 4", "SD Card Reader"))
                .build();

        when(laptopService.getLaptopById(laptopId)).thenReturn(response);

        mockMvc.perform(get("/api/laptops/{laptopId}", laptopId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(laptopId.toString()))
                .andExpect(jsonPath("$.model").value("Dell XPS 15"))
                .andExpect(jsonPath("$.manufacturer.name").value("Dell"));
    }

    @Test
    void getByIdShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        when(laptopService.getLaptopById(nonExistentId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Laptop not found"));

        mockMvc.perform(get("/api/laptops/{laptopId}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldUpdateLaptop_WhenRequestIsValid() throws Exception {
        UUID laptopId = UUID.randomUUID();

        AddLaptopDto updateRequest = AddLaptopDto.builder()
                .model("MacBook Pro M3")
                .price(2999.99)
                .ram(64)
                .storage(2048)
                .manufacturerId(UUID.randomUUID())
                .ports(List.of("Thunderbolt 4", "HDMI 2.1"))
                .build();

        LaptopResponseDto updatedResponse = LaptopResponseDto.builder()
                .id(laptopId)
                .model(updateRequest.getModel())
                .price(updateRequest.getPrice())
                .ram(updateRequest.getRam())
                .storage(updateRequest.getStorage())
                .manufacturer(ManufacturerResponseDto.builder()
                        .id(updateRequest.getManufacturerId())
                        .name("Apple")
                        .build())
                .ports(updateRequest.getPorts())
                .build();

        when(laptopService.updateLaptop(eq(laptopId), any(AddLaptopDto.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/laptops/{laptopId}", laptopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(laptopId.toString()))
                .andExpect(jsonPath("$.model").value("MacBook Pro M3"))
                .andExpect(jsonPath("$.storage").value(2048));
    }

    @Test
    void updateShouldReturnBadRequest_WhenUpdateDataIsInvalid() throws Exception {
        UUID laptopId = UUID.randomUUID();

        AddLaptopDto invalidRequest = AddLaptopDto.builder()
                .model("")
                .price(-50.0)
                .ram(null)
                .storage(32)
                .manufacturerId(null)
                .build();

        mockMvc.perform(put("/api/laptops/{laptopId}", laptopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeShouldDeleteLaptop_WhenIdExists() throws Exception {
        UUID laptopId = UUID.randomUUID();

        mockMvc.perform(delete("/api/laptops/{laptopId}", laptopId))
                .andExpect(status().isNoContent());

        verify(laptopService).removeLaptopById(laptopId);
    }

    @Test
    void removeShouldReturnNotFound_WhenLaptopDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Laptop not found"))
                .when(laptopService).removeLaptopById(nonExistentId);

        mockMvc.perform(delete("/api/laptops/{laptopId}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllShouldReturnPagedResponse_WhenRequestIsValid() throws Exception {
        LaptopSearchRequest searchRequest = LaptopSearchRequest.builder()
                .manufacturers(List.of(UUID.randomUUID()))
                .ram(16)
                .storage(512)
                .page(1)
                .size(10)
                .build();

        LaptopShortResponseDto laptopDto = LaptopShortResponseDto.builder()
                .id(UUID.randomUUID())
                .model("Lenovo ThinkPad X1")
                .price(2100.00)
                .build();

        PageResponse<LaptopShortResponseDto> pageResponse = new PageResponse<>(List.of(laptopDto), 1);
        when(laptopService.getAllLaptops(any(LaptopSearchRequest.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(post("/api/laptops/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].model").value("Lenovo ThinkPad X1"))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getAllShouldReturnBadRequest_WhenSearchCriteriaAreInvalid() throws Exception {
        LaptopSearchRequest invalidRequest = LaptopSearchRequest.builder()
                .ram(-16)
                .storage(0)
                .page(0)
                .size(-5)
                .build();

        mockMvc.perform(post("/api/laptops/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void importFromJsonShouldImportLaptops_WhenFileIsProvided() throws Exception {
        MockMultipartFile jsonFile = new MockMultipartFile(
                "file",
                "laptops.json",
                MediaType.APPLICATION_JSON_VALUE,
                "[{\"model\": \"Test\"}]".getBytes()
        );

        ImportResultDto expectedResult = ImportResultDto.builder()
                .successfulCount(50)
                .failedCount(2)
                .build();

        when(laptopService.importData(any(MultipartFile.class), eq(ImportType.JSON)))
                .thenReturn(expectedResult);

        mockMvc.perform(multipart("/api/laptops/upload").file(jsonFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successfulCount").value(50))
                .andExpect(jsonPath("$.failedCount").value(2));
    }

    @Test
    void importFromJsonShouldReturnBadRequest_WhenFileIsMissing() throws Exception {
        mockMvc.perform(multipart("/api/laptops/upload"))
                .andExpect(status().isBadRequest());
    }

}

