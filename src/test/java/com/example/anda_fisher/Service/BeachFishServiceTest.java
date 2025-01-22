package com.example.anda_fisher.Service;

import com.example.anda_fisher.Model.Beach;
import com.example.anda_fisher.Model.Fish;
import com.example.anda_fisher.Repository.BeachFishRepository;
import com.example.anda_fisher.Repository.BeachRepository;
import com.example.anda_fisher.Repository.FishRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeachFishServiceTest {

    @Mock
    private BeachRepository beachRepository;

    @Mock
    private FishRepository fishRepository;

    @Mock
    private BeachFishRepository beachFishRepository;

    @InjectMocks
    private BeachFishService beachFishService;

    @Test
    void addFishToBeach_Success() {
        Beach beach = new Beach();
        beach.setId(1L);

        Fish fish1 = new Fish();
        fish1.setId(1L);

        Fish fish2 = new Fish();
        fish2.setId(2L);

        List<Long> fishIds = List.of(1L, 2L);

        when(beachRepository.findById(1L)).thenReturn(Optional.of(beach));
        when(fishRepository.findAllById(fishIds)).thenReturn(List.of(fish1, fish2));

        beachFishService.addFishToBeach(1L, fishIds);

        verify(beachFishRepository, times(1)).saveAll(anyList());
    }
}
