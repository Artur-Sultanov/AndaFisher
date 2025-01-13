package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.Beach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BeachRepository extends JpaRepository<Beach, Long>, JpaSpecificationExecutor<Beach> {
}