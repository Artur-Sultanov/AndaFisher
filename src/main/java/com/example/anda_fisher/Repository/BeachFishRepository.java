package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.BeachFish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeachFishRepository extends JpaRepository<BeachFish, Long> {
}

