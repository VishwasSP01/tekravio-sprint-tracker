package com.tekravio.tracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BonusFeaturesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginReturnsJwtAndBearerTokenAccessesApi() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"Admin@123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andReturn().getResponse().getContentAsString();

        String token = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response).path("data").path("token").asText();

        mockMvc.perform(get("/api/clients").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void protectedApiRejectsAnonymousRequestsButSwaggerRemainsPublic() throws Exception {
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Tekravio Project & Sprint Tracker API"));
    }

    @Test
    @WithMockUser(username = "aarav", roles = "ENGINEER")
    void engineerCanUpdateOwnTaskAndHistoryRecordsActor() throws Exception {
        mockMvc.perform(put("/api/tasks/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"REVIEW\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REVIEW"));

        mockMvc.perform(get("/api/tasks/2/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].previousStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data[0].newStatus").value("REVIEW"))
                .andExpect(jsonPath("$.data[0].changedBy").value("aarav"));
    }

    @Test
    @WithMockUser(username = "aarav", roles = "ENGINEER")
    void engineerCannotCreateClientsOrUpdateAnotherEngineersTask() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Blocked",
                                  "industry":"Tech",
                                  "contactEmail":"blocked@example.com",
                                  "country":"India"
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/tasks/5/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Engineers can only update tasks assigned to themselves"));
    }
}
