package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.Beach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeachRepository extends JpaRepository<Beach, Long>, JpaSpecificationExecutor<Beach> {
    boolean existsByNameAndLocation(String name, String location);
    Optional<Beach> findByName(String name);  // ✅ Добавлен метод
}