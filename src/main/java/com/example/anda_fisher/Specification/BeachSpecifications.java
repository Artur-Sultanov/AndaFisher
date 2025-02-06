package com.example.anda_fisher.Specification;

import com.example.anda_fisher.Filter.BeachFilter;
import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Model.WaterType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class BeachSpecifications {

    public static Specification<Beach> filterBy(BeachFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getWaterType() != null && !filter.getWaterType().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("waterType"),
                        WaterType.valueOf(filter.getWaterType().toUpperCase())
                ));
            }

            if (filter.getLocation() != null && !filter.getLocation().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("location")),
                        "%" + filter.getLocation().toLowerCase() + "%"
                ));
            }

            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
