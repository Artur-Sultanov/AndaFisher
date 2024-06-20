package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.Fish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FishRepository extends JpaRepository<Fish, Long> {
}