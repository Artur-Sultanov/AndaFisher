package com.example.anda_fisher.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "beach_fish")
public class BeachFish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "beach_id", nullable = false)
    @JsonBackReference
    private Beach beach;

    @ManyToOne
    @JoinColumn(name = "fish_id", nullable = false)
    private Fish fish;
}

