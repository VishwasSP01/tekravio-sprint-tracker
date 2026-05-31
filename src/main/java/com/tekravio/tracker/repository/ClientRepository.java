package com.tekravio.tracker.repository;

import com.tekravio.tracker.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByIdAndDeletedFalse(Long id);
    Page<Client> findAllByDeletedFalse(Pageable pageable);
}
