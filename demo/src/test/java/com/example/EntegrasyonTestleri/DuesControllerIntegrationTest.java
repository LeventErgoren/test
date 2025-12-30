package com.example.EntegrasyonTestleri;

import com.example.controller.DuesController;
import com.example.entity.Dues;
import com.example.service.DuesService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DuesController.class)
public class DuesControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    DuesService duesService;

    @Test
    public void getAllDuesTest() throws Exception {
        var dues = new com.example.entity.Dues();
        when(duesService.getAllDues()).thenReturn(List.of(dues, dues));

        mockMvc.perform(get("/api/dues")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void assignDuesToFlatTest() throws Exception {
        Long flatId = 1L;
        Dues dues = new Dues();
        dues.setId(10L);
        when(duesService.assignDuesToFlat(anyLong(), any(Dues.class))).thenReturn(dues);

        mockMvc.perform(post("/api/dues/flat/{flatId}", flatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dues)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }
}
