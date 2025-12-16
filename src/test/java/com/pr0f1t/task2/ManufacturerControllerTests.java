package com.pr0f1t.task2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pr0f1t.task2.controller.ManufacturerController;
import com.pr0f1t.task2.dto.manufacturer.AddManufacturerDto;
import com.pr0f1t.task2.dto.manufacturer.ManufacturerResponseDto;
import com.pr0f1t.task2.service.ManufacturerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManufacturerController.class)
public class ManufacturerControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ManufacturerService manufacturerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllShouldReturnListOfManufacturers_WhenDataExists() throws Exception {
        ManufacturerResponseDto manufacturer = ManufacturerResponseDto.builder()
                .id(UUID.randomUUID())
                .name("Asus")
                .build();

        when(manufacturerService.getAllManufacturers()).thenReturn(List.of(manufacturer));

        mockMvc.perform(get("/api/manufacturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Asus"));
    }

    @Test
    void getAllShouldReturnEmptyList_WhenNoManufacturersFound() throws Exception {
        when(manufacturerService.getAllManufacturers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/manufacturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void addShouldCreateManufacturer_WhenRequestIsValid() throws Exception {
        AddManufacturerDto request = AddManufacturerDto.builder()
                .name("HP")
                .build();

        ManufacturerResponseDto response = ManufacturerResponseDto.builder()
                .id(UUID.randomUUID())
                .name("HP")
                .build();

        when(manufacturerService.addManufacturer(any(AddManufacturerDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("HP"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void addShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        AddManufacturerDto invalidRequest = AddManufacturerDto.builder()
                .name("")
                .build();

        mockMvc.perform(post("/api/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldUpdateManufacturer_WhenRequestIsValid() throws Exception {
        UUID id = UUID.randomUUID();
        AddManufacturerDto request = AddManufacturerDto.builder()
                .name("Lenovo Updated")
                .build();

        ManufacturerResponseDto response = ManufacturerResponseDto.builder()
                .id(id)
                .name("Lenovo Updated")
                .build();

        when(manufacturerService.updateManufacturer(eq(id), any(AddManufacturerDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/manufacturers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lenovo Updated"));
    }

    @Test
    void updateShouldReturnNotFound_WhenUpdatingNonExistentManufacturer() throws Exception {
        UUID id = UUID.randomUUID();
        AddManufacturerDto request = AddManufacturerDto.builder()
                .name("Unknown Brand")
                .build();

        when(manufacturerService.updateManufacturer(eq(id), any(AddManufacturerDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/manufacturers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeShouldRemoveManufacturer_WhenIdExists() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/manufacturers/{id}", id))
                .andExpect(status().isNoContent());

        verify(manufacturerService).removeManufacturerById(id);
    }

    @Test
    void removeShouldReturnNotFound_WhenDeletingNonExistentManufacturer() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(manufacturerService).removeManufacturerById(id);

        mockMvc.perform(delete("/api/manufacturers/{id}", id))
                .andExpect(status().isNotFound());
    }

}
