package com.example.anda_fisher.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FishDTO {
    private Long id;
    private String name;
    private boolean approved;
}
