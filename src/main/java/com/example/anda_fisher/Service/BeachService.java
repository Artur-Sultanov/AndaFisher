package com.example.anda_fisher.Service;

import com.example.anda_fisher.DTO.BeachDTO;
import com.example.anda_fisher.Exception.ConflictException;
import com.example.anda_fisher.Exception.ResourceNotFoundException;
import com.example.anda_fisher.Filter.BeachFilter;
import com.example.anda_fisher.Mapper.BeachMapper;
import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Repository.BeachRepository;
import com.example.anda_fisher.Specification.BeachSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeachService {

    private final BeachRepository beachRepository;

    public List<Beach> getAllBeaches() {
        return beachRepository.findAll()
                .stream()
                .filter(Beach::isApproved)  // Показываем только одобренные пляжи
                .collect(Collectors.toList());
    }

    public Beach getBeachById(Long id) {
        return beachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("❌ Beach not found with id: " + id));
    }

    public Beach addBeach(Beach beach) {
        if (beachRepository.existsByNameAndLocation(beach.getName(), beach.getLocation())) {
            throw new ConflictException("⚠️ A beach with the same name and location already exists.");
        }
        return beachRepository.save(beach);
    }

    public Beach updateBeach(Long id, BeachDTO beachDTO) {
        Beach existingBeach = beachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("❌ Beach not found with ID: " + id));

        if (beachRepository.existsByNameAndLocation(beachDTO.getName(), beachDTO.getLocation())
                && (!existingBeach.getName().equals(beachDTO.getName())
                || !existingBeach.getLocation().equals(beachDTO.getLocation()))) {
            throw new ConflictException("⚠️ A beach with the same name and location already exists.");
        }

        BeachMapper.updateEntity(existingBeach, beachDTO);

        return beachRepository.save(existingBeach);
    }


    public void deleteBeach(Long id) {
        Beach beach = getBeachById(id);
        beachRepository.delete(beach);
    }

    public List<Beach> filterBeaches(BeachFilter filter) {
        return beachRepository.findAll(BeachSpecifications.filterBy(filter));
    }
}

