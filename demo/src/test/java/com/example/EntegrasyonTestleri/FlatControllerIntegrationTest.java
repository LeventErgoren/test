package com.example.EntegrasyonTestleri;

import com.example.controller.FlatController;
import com.example.entity.Flat;
import com.example.service.FlatService;
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

@WebMvcTest(FlatController.class)
public class FlatControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    FlatService flatService;

    @Test
    public void getAllFlatsTest() throws Exception {
        Flat flat = new Flat();
        when(flatService.getAllFlats()).thenReturn(List.of(flat, flat));

        mockMvc.perform(get("/api/flats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getFlatByIdTest() throws Exception {
        Flat flat = new Flat();
        flat.setId(1L);

        when(flatService.getFlatById(1L)).thenReturn(flat);

        mockMvc.perform(get("/api/flats/{id}", flat.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void createFlatTest() throws Exception {
        Flat flat = new Flat();
        Flat dbFlat = new Flat();
        dbFlat.setId(1L);

        when(flatService.saveFlat(any(Flat.class))).thenReturn(dbFlat);

        mockMvc.perform(post("/api/flats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flat)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

}