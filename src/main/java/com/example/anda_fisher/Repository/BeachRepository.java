package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.Beach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeachRepository extends JpaRepository<Beach, Long>, JpaSpecificationExecutor<Beach> {
    boolean existsByNameAndLocation(String name, String location);

    @Query("SELECT COUNT(b) > 0 FROM Beach b WHERE b.name = :name AND b.location = :location AND b.id != :id")
    boolean existsByNameAndLocationExcludingId(@Param("name") String name, @Param("location") String location, @Param("id") Long id);

    Optional<Beach> findByName(String name);

    Optional<Beach> findByImagePath(String imagePath);
}
