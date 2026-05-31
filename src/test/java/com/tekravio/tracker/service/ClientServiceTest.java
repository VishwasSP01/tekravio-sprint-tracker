package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.ClientDto;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Client;
import com.tekravio.tracker.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientService service;

    @Test
    void supportsCreateReadUpdateListAndSoftDelete() {
        Client client = client();
        ClientDto.Request request = new ClientDto.Request("Acme Updated", "Tech", "ops@acme.example", "India");
        PageRequest pageable = PageRequest.of(0, 10);
        when(repository.save(org.mockito.ArgumentMatchers.any(Client.class))).thenReturn(client);
        when(repository.findAllByDeletedFalse(pageable)).thenReturn(new PageImpl<>(List.of(client)));
        when(repository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(client));

        assertThat(service.create(request).name()).isEqualTo("Acme");
        assertThat(service.list(pageable).totalElements()).isEqualTo(1);
        assertThat(service.get(1L).name()).isEqualTo("Acme");
        assertThat(service.update(1L, request).name()).isEqualTo("Acme Updated");

        service.delete(1L);

        assertThat(client.isDeleted()).isTrue();
    }

    @Test
    void get_whenMissing_throwsNotFound() {
        when(repository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L)).isInstanceOf(ResourceNotFoundException.class);
        verify(repository).findByIdAndDeletedFalse(99L);
    }

    private Client client() {
        return new Client("Acme", "Tech", "ops@acme.example", "India");
    }
}
