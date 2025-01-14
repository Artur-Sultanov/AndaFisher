package com.example.anda_fisher.Service;

import com.example.anda_fisher.Filter.BeachFilter;
import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Model.WaterType;
import com.example.anda_fisher.Repository.BeachRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeachService {
    @Autowired
    private BeachRepository beachRepository;

    public List<Beach> getAllBeaches() {
        return beachRepository.findAll();
    }

    public Beach getBeachById(Long id) {
        return beachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beach not found with id: " + id));
    }

    public Beach saveBeach(Beach beach) {
        return beachRepository.save(beach);
    }

    public void deleteBeach(Long id) {
        if (!beachRepository.existsById(id)) {
            throw new RuntimeException("Beach not found with id: " + id);
        }
        beachRepository.deleteById(id);
    }

    public List<Beach> filterBeaches(BeachFilter filter) {
        return beachRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by water type (if provided)
            if (filter.getWaterType() != null && !filter.getWaterType().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("waterType"),
                        WaterType.valueOf(filter.getWaterType().toUpperCase())
                ));
            }

            // Filter by location (case-insensitive, partial match)
            if (filter.getLocation() != null && !filter.getLocation().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("location")),
                        "%" + filter.getLocation().toLowerCase() + "%"
                ));
            }

            // Filter by beach name (case-insensitive, partial match)
            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"
                ));
            }

            // Combine all conditions using AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

