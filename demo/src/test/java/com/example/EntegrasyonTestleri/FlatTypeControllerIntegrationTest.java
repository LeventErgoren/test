package com.example.EntegrasyonTestleri;

import com.example.controller.FlatTypeController;
import com.example.entity.FlatType;
import com.example.service.FlatTypeService;
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

@WebMvcTest(FlatTypeController.class)
public class FlatTypeControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    FlatTypeService flatTypeService;

    @Test
    public void getAllFlatTypesTest() throws Exception {
        FlatType flatType = new FlatType();
        when(flatTypeService.getAllFlatTypes()).thenReturn(List.of(flatType, flatType));

        mockMvc.perform(get("/api/flat-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void createFlatTypeTest() throws Exception {
        FlatType flatType = new FlatType();
        FlatType dbFlatType = new FlatType();
        dbFlatType.setId(1L);
        when(flatTypeService.saveFlatType(any(FlatType.class))).thenReturn(dbFlatType);

        mockMvc.perform(post("/api/flat-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flatType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

}
