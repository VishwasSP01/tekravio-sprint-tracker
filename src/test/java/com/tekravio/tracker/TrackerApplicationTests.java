package com.tekravio.tracker;

import com.tekravio.tracker.repository.ClientRepository;
import com.tekravio.tracker.repository.EngineerRepository;
import com.tekravio.tracker.repository.ProjectRepository;
import com.tekravio.tracker.repository.SprintRepository;
import com.tekravio.tracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TrackerApplicationTests {

    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final EngineerRepository engineerRepository;
    private final TaskRepository taskRepository;

    @Autowired
    TrackerApplicationTests(
            ClientRepository clientRepository,
            ProjectRepository projectRepository,
            SprintRepository sprintRepository,
            EngineerRepository engineerRepository,
            TaskRepository taskRepository) {
        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.engineerRepository = engineerRepository;
        this.taskRepository = taskRepository;
    }

    @Test
    void contextLoadsWithRequiredSeedData() {
        assertThat(clientRepository.count()).isEqualTo(3);
        assertThat(projectRepository.count()).isEqualTo(2);
        assertThat(sprintRepository.count()).isEqualTo(2);
        assertThat(engineerRepository.count()).isEqualTo(3);
        assertThat(taskRepository.count()).isEqualTo(5);
    }
}
