package com.arthurpv15.apimanagement.controller;

import com.arthurpv15.apimanagement.dto.LoginRequest;
import com.arthurpv15.apimanagement.dto.LoginResponse;
import com.arthurpv15.apimanagement.services.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("teste")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    private String token;

    @BeforeEach
    void setUp() {
        LoginResponse response = authService.login(new LoginRequest("arthur@example.com", "123456"));
        token = response.token();
    }

    @Test
    void findAll_withToken_returnsOk() throws Exception {
        mockMvc.perform(get("/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    void findAll_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findById_withValidId_returnsCategory() throws Exception {
        mockMvc.perform(get("/categories/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Shopping"));
    }

    @Test
    void findById_withInvalidId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/categories/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
