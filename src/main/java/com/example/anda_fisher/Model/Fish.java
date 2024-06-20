package com.example.anda_fisher.Model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Fish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "fish")
    private Set<Beach> beaches = new HashSet<>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Beach> getBeaches() {
        return beaches;
    }

    public void setBeaches(Set<Beach> beaches) {
        this.beaches = beaches;
    }
}