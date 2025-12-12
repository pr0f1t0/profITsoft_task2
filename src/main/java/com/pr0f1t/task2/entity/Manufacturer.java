package com.pr0f1t.task2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity(name = "manufacturers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Manufacturer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "manufacturer",  cascade = CascadeType.ALL,  orphanRemoval = true)
    public List<Laptop> laptops;

}
