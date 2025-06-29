package ru.volodin.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.volodin.deal.entity.ClientEntity;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
}
