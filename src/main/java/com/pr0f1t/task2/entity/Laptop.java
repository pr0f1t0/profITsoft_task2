package com.pr0f1t.task2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity(name = "laptops")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Laptop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String model;

    private Double price;

    private Integer ram;

    private Integer storage;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> ports;

}
