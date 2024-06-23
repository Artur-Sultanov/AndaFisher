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
    private String description;

    public Beach(Long id, String name, String location, String description, Set<Fish> fish) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.fish = fish;
    }

    public Beach() {
    }

    @ManyToMany
    @JoinTable(
            name = "beach_fish",
            joinColumns = @JoinColumn(name = "beach_id"),
            inverseJoinColumns = @JoinColumn(name = "fish_id"))
    @JsonIgnore
    private Set<Fish> fish = new HashSet<>();


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
    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }
    public Set<Fish> getFish() {
        return fish;
    }
    public void setFish(Set<Fish> fish) {
        this.fish = fish;
    }
}