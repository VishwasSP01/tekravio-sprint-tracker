package com.tekravio.tracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin", roles = "ADMIN")
class TrackerApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createClient_returnsCreatedWrappedResponse() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Atlas Corp",
                                  "industry": "Technology",
                                  "contactEmail": "delivery@atlas.example",
                                  "country": "India"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Atlas Corp"));
    }

    @Test
    void createClient_whenInvalid_returnsFieldErrors() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "industry": "Technology", "contactEmail": "bad", "country": "India"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void listClients_supportsPaginationAndSoftDelete() throws Exception {
        mockMvc.perform(delete("/api/clients/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/clients?page=0&size=10&sort=name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2));

        mockMvc.perform(get("/api/clients/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void taskRules_returnBadRequestsForUnavailableEngineerAndBackwardsStatus() throws Exception {
        mockMvc.perform(put("/api/tasks/2/assign/3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Engineer 3 is not available for assignment"));

        mockMvc.perform(put("/api/tasks/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"TODO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Task status must advance exactly one step from IN_PROGRESS to TODO"));
    }

    @Test
    void intelligenceAndFilteringEndpoints_returnStructuredData() throws Exception {
        mockMvc.perform(get("/api/tasks?status=IN_PROGRESS&priority=CRITICAL&sprintId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));

        mockMvc.perform(get("/api/sprints/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalTasks").value(3));

        mockMvc.perform(get("/api/engineers/1/workload"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activeTasks").value(2));

        mockMvc.perform(get("/api/projects/1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").isNumber());

        mockMvc.perform(get("/api/engineers/available?stack=JAVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void crudSurfaces_returnDtosWithoutExposingEntities() throws Exception {
        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Northstar Bank"));
        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Northstar Bank Updated",
                                  "industry": "Financial Services",
                                  "contactEmail": "delivery@northstar.example",
                                  "country": "India"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Northstar Bank Updated"));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2));
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.clientId").value(1));
        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Digital Banking API",
                                  "description": "Updated API modernization scope",
                                  "status": "ACTIVE",
                                  "startDate": "2026-05-01",
                                  "endDate": "2026-12-31",
                                  "clientId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("Updated API modernization scope"));
        mockMvc.perform(get("/api/projects/1/sprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));

        mockMvc.perform(get("/api/sprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2));
        mockMvc.perform(get("/api/sprints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.projectId").value(1));
        mockMvc.perform(put("/api/sprints/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sprintNumber": 1,
                                  "goal": "Updated sprint goal",
                                  "status": "IN_PROGRESS",
                                  "startDate": "2026-05-01",
                                  "endDate": "2026-06-30",
                                  "projectId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.goal").value("Updated sprint goal"));
        mockMvc.perform(get("/api/sprints/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(3));

        mockMvc.perform(get("/api/engineers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(3));
        mockMvc.perform(get("/api/engineers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.primaryStack").value("JAVA"));
        mockMvc.perform(put("/api/engineers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Aarav Sharma",
                                  "email": "aarav@tekravio.example",
                                  "primaryStack": "JAVA",
                                  "experienceYears": 5,
                                  "available": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.experienceYears").value(5));

        mockMvc.perform(get("/api/tasks/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sprintId").value(1));
        mockMvc.perform(put("/api/tasks/3/assign/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.assignedEngineerId").value(1));
        mockMvc.perform(put("/api/tasks/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }
}
