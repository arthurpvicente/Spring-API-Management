package com.arthurpv15.apimanagement.controller;

import com.arthurpv15.apimanagement.dto.LoginRequest;
import com.arthurpv15.apimanagement.dto.LoginResponse;
import com.arthurpv15.apimanagement.dto.UserRequest;
import com.arthurpv15.apimanagement.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("teste")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void findAll_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findById_withValidId_returnsUser() throws Exception {
        mockMvc.perform(get("/users/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Arthur"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void findById_withInvalidId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/users/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    void insert_withValidData_returnsCreated() throws Exception {
        UserRequest request = new UserRequest("New User", "newuser@example.com", "password123");

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void insert_withInvalidData_returnsBadRequest() throws Exception {
        UserRequest request = new UserRequest("", "not-an-email", "12");

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").isNotEmpty());
    }

    @Test
    void delete_withOwnId_returnsNoContent() throws Exception {
        UserRequest newUser = new UserRequest("ToDelete", "todelete@example.com", "password123");
        String response = mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andReturn().getResponse().getContentAsString();

        Long newId = objectMapper.readTree(response).get("id").asLong();

        LoginResponse newUserLogin = authService.login(new LoginRequest("todelete@example.com", "password123"));

        mockMvc.perform(delete("/users/" + newId)
                        .header("Authorization", "Bearer " + newUserLogin.token()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_otherUser_returnsForbidden() throws Exception {
        mockMvc.perform(delete("/users/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_withInvalidId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/users/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
