package com.pr0f1t.task2.repository;

import com.pr0f1t.task2.entity.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, UUID> {
    Optional<Manufacturer> findByName(String name);
}
