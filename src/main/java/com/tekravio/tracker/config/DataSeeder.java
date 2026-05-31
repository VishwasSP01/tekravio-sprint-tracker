package com.tekravio.tracker.config;

import com.tekravio.tracker.model.Client;
import com.tekravio.tracker.model.Engineer;
import com.tekravio.tracker.model.PrimaryStack;
import com.tekravio.tracker.model.Project;
import com.tekravio.tracker.model.ProjectStatus;
import com.tekravio.tracker.model.Sprint;
import com.tekravio.tracker.model.SprintStatus;
import com.tekravio.tracker.model.Task;
import com.tekravio.tracker.model.TaskPriority;
import com.tekravio.tracker.model.TaskStatus;
import com.tekravio.tracker.repository.ClientRepository;
import com.tekravio.tracker.repository.EngineerRepository;
import com.tekravio.tracker.repository.ProjectRepository;
import com.tekravio.tracker.repository.SprintRepository;
import com.tekravio.tracker.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("!test & !prod")
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            ClientRepository clientRepository,
            ProjectRepository projectRepository,
            SprintRepository sprintRepository,
            EngineerRepository engineerRepository,
            TaskRepository taskRepository) {
        return args -> {
            if (clientRepository.count() > 0) {
                return;
            }

            Client financeClient = new Client("Northstar Bank", "Financial Services",
                    "delivery@northstar.example", "India");
            Client retailClient = new Client("ShopSphere", "Retail",
                    "platform@shopsphere.example", "United States");
            Client logisticsClient = new Client("SwiftRoute", "Logistics",
                    "operations@swiftroute.example", "Singapore");
            clientRepository.saveAll(List.of(financeClient, retailClient, logisticsClient));

            Project bankingApi = new Project("Digital Banking API", "Modernize customer banking APIs",
                    ProjectStatus.ACTIVE, LocalDate.now().minusDays(14), null, financeClient);
            Project retailInsights = new Project("Retail Insights", "Build operational analytics services",
                    ProjectStatus.ACTIVE, LocalDate.now().minusDays(7), null, retailClient);
            projectRepository.saveAll(List.of(bankingApi, retailInsights));

            Sprint bankingSprint = new Sprint(1, "Deliver account and transaction foundations",
                    SprintStatus.IN_PROGRESS, LocalDate.now().minusDays(7), LocalDate.now().plusDays(7),
                    bankingApi);
            Sprint retailSprint = new Sprint(1, "Create the analytics ingestion pipeline",
                    SprintStatus.PLANNED, LocalDate.now(), LocalDate.now().plusDays(14), retailInsights);
            sprintRepository.saveAll(List.of(bankingSprint, retailSprint));

            Engineer javaEngineer = new Engineer("Aarav Sharma", "aarav@tekravio.example",
                    PrimaryStack.JAVA, 4, true);
            Engineer reactEngineer = new Engineer("Diya Rao", "diya@tekravio.example",
                    PrimaryStack.REACT, 3, true);
            Engineer qaEngineer = new Engineer("Kabir Singh", "kabir@tekravio.example",
                    PrimaryStack.QA, 5, false);
            engineerRepository.saveAll(List.of(javaEngineer, reactEngineer, qaEngineer));

            taskRepository.saveAll(List.of(
                    task("Design account schema", TaskPriority.HIGH, TaskStatus.DONE, "8", "7",
                            bankingSprint, javaEngineer),
                    task("Implement transaction search", TaskPriority.CRITICAL, TaskStatus.IN_PROGRESS, "14",
                            null, bankingSprint, javaEngineer),
                    task("Add API contract tests", TaskPriority.HIGH, TaskStatus.TODO, "6", null,
                            bankingSprint, qaEngineer),
                    task("Build ingestion endpoint", TaskPriority.HIGH, TaskStatus.TODO, "12", null,
                            retailSprint, javaEngineer),
                    task("Create dashboard wireframe", TaskPriority.MEDIUM, TaskStatus.TODO, "5", null,
                            retailSprint, reactEngineer)
            ));
        };
    }

    private Task task(String title, TaskPriority priority, TaskStatus status, String estimatedHours,
                      String actualHours, Sprint sprint, Engineer engineer) {
        return new Task(title, title + " for the current sprint", priority, status,
                new BigDecimal(estimatedHours),
                actualHours == null ? null : new BigDecimal(actualHours),
                sprint, engineer);
    }
}
