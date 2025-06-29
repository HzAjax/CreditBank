package ru.volodin.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.volodin.deal.entity.StatementEntity;

import java.util.UUID;

public interface StatementRepository extends JpaRepository<StatementEntity, UUID> {
}
