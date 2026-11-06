package com.example.dealerservice.service;

import com.example.dealerservice.exception.ResourceNotFoundException;
import com.example.dealerservice.model.DealerShipModel;
import com.example.dealerservice.repository.DealerShipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealerShipServiceTest {

    private DealerShipRepository repository;
    private DealerShipService service;

    @BeforeEach
    void setUp() {
        repository = mock(DealerShipRepository.class);
        service = new DealerShipService(repository);
    }

    @Test
    void getByIdReturnsDealer() {
        DealerShipModel dealer = new DealerShipModel();
        when(repository.findById(1)).thenReturn(Optional.of(dealer));

        assertSame(dealer, service.getById(1));
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99));
    }

    @Test
    void createResetsIdBeforeSaving() {
        DealerShipModel dealer = new DealerShipModel();
        dealer.setId(55);
        when(repository.save(dealer)).thenReturn(dealer);

        service.create(dealer);

        assertNull(dealer.getId());
        verify(repository).save(dealer);
    }

    @Test
    void updateSetsPathIdAndSaves() {
        DealerShipModel dealer = new DealerShipModel();
        when(repository.existsById(3)).thenReturn(true);
        when(repository.save(dealer)).thenReturn(dealer);

        service.update(3, dealer);

        assertEquals(3, dealer.getId());
        verify(repository).save(dealer);
    }

    @Test
    void updateThrowsWhenMissing() {
        when(repository.existsById(3)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(3, new DealerShipModel()));
        verify(repository, never()).save(any());
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(repository.existsById(3)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(3));
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void deleteRemovesExisting() {
        when(repository.existsById(3)).thenReturn(true);

        service.delete(3);

        verify(repository).deleteById(3);
    }

    @Test
    void searchTrimsKeywordAndHandlesNull() {
        when(repository.search("")).thenReturn(List.of());
        when(repository.search("Москва")).thenReturn(List.of(new DealerShipModel()));

        assertEquals(0, service.search(null).size());
        assertEquals(1, service.search("  Москва ").size());
    }
}
