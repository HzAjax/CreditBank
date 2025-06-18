package ru.volodin.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.volodin.deal.entity.Client;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
}
