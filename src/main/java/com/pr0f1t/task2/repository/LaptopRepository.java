package com.pr0f1t.task2.repository;

import com.pr0f1t.task2.entity.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface LaptopRepository extends JpaRepository<Laptop, UUID>, JpaSpecificationExecutor<Laptop> {
}
