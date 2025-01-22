package com.example.anda_fisher.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(
        name = "beach",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "location"}),
        indexes = {
                @Index(name = "idx_beach_name", columnList = "name"),
                @Index(name = "idx_beach_location", columnList = "location")
        }
)
public class Beach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    @Column(nullable = false)
    @NotBlank(message = "Location cannot be empty")
    private String location;
    @Column
    private String description;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column
    @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
    @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
    private Double latitude;
    @Column
    @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
    @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
    private Double longitude;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Water type must be specified")
    private WaterType waterType = WaterType.SALTWATER;
    @Column(unique = true)
    @Basic(fetch = FetchType.LAZY)
    private String imagePath;

    @Column(nullable = false)
    private boolean approved = false;

    @OneToMany(mappedBy = "beach", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<BeachFish> beachFish = new HashSet<>();
}