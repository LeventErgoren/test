package com.example.EntegrasyonTestleri;

import com.example.controller.ResidentController;
import com.example.entity.Resident;
import com.example.service.ResidentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResidentController.class)
public class ResidentControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ResidentService residentService;

    @Test
    public void getAllResidentsTest() throws Exception {
        Resident resident = new Resident();
        when(residentService.getAllResidents()).thenReturn(List.of(resident, resident));

        mockMvc.perform(get("/api/residents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getResidentByIdTest() throws Exception {
        Long id = 1L;
        Resident resident = new Resident();
        resident.setId(id);
        when(residentService.getResidentById(id)).thenReturn(resident);

        mockMvc.perform(get("/api/residents/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    public void createResidentTest() throws Exception {
        Resident resident = new Resident();
        Resident dbResident = new Resident();
        dbResident.setId(5L);
        when(residentService.saveResident(any(Resident.class))).thenReturn(dbResident);

        mockMvc.perform(post("/api/residents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resident)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }
}
