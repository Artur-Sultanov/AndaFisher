package com.example.anda_fisher.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Beach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @ManyToMany
    @JoinTable(
            name = "beach_fish",
            joinColumns = @JoinColumn(name = "beach_id"),
            inverseJoinColumns = @JoinColumn(name = "fish_id"))
    @JsonIgnore
    private Set<Fish> fish = new HashSet<>();

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<Fish> getFish() {
        return fish;
    }

    public void setFish(Set<Fish> fish) {
        this.fish = fish;
    }
}