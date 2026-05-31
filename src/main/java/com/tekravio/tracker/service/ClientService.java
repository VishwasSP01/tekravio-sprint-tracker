package com.tekravio.tracker.service;

import com.tekravio.tracker.dto.ClientDto;
import com.tekravio.tracker.dto.PageResponse;
import com.tekravio.tracker.exception.ResourceNotFoundException;
import com.tekravio.tracker.model.Client;
import com.tekravio.tracker.repository.ClientRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public ClientDto.Response create(ClientDto.Request request) {
        return DtoMapper.toResponse(repository.save(
                new Client(request.name(), request.industry(), request.contactEmail(), request.country())));
    }

    @Transactional(readOnly = true)
    public PageResponse<ClientDto.Response> list(Pageable pageable) {
        return PageResponse.from(repository.findAllByDeletedFalse(pageable), DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClientDto.Response get(Long id) {
        return DtoMapper.toResponse(findActive(id));
    }

    public ClientDto.Response update(Long id, ClientDto.Request request) {
        Client client = findActive(id);
        client.setName(request.name());
        client.setIndustry(request.industry());
        client.setContactEmail(request.contactEmail());
        client.setCountry(request.country());
        return DtoMapper.toResponse(client);
    }

    public void delete(Long id) {
        findActive(id).setDeleted(true);
    }

    Client findActive(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }
}
