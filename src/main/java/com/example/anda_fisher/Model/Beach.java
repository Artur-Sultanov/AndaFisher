package com.example.anda_fisher.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "beach", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "location"}))
public class Beach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)

    private String name;
    @Column(nullable = false)
    private String location;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column
    private Double latitude;
    @Column
    private Double longitude;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaterType waterType;
    @Column(unique = true)
    private String imagePath;

    @OneToMany(mappedBy = "beach", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<BeachFish> beachFish = new HashSet<>();

}

