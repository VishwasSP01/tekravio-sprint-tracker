package com.tekravio.tracker.repository;

import com.tekravio.tracker.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
