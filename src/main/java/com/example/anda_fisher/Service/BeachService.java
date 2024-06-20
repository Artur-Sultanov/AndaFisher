package com.example.anda_fisher.Service;

import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Repository.BeachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeachService {
    @Autowired
    private BeachRepository beachRepository;

    public List<Beach> getAllBeaches() {
        return beachRepository.findAll();
    }

    public Beach getBeachById(Long id) {
        return beachRepository.findById(id).orElse(null);
    }

    public Beach saveBeach(Beach beach) {
        return beachRepository.save(beach);
    }

    public void deleteBeach(Long id) {
        beachRepository.deleteById(id);
    }
}

