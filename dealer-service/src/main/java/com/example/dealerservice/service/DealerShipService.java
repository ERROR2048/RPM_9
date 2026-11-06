package com.example.dealerservice.service;

import com.example.dealerservice.exception.ResourceNotFoundException;
import com.example.dealerservice.model.DealerShipModel;
import com.example.dealerservice.repository.DealerShipRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealerShipService {

    private final DealerShipRepository repository;

    public DealerShipService(DealerShipRepository repository) {
        this.repository = repository;
    }

    public List<DealerShipModel> getAll() {
        return repository.findAll();
    }

    public List<DealerShipModel> getPage(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    public List<DealerShipModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }

    public DealerShipModel getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dealer not found with id: " + id));
    }

    public DealerShipModel create(DealerShipModel item) {
        item.setId(null);
        return repository.save(item);
    }

    public DealerShipModel update(int id, DealerShipModel item) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Dealer not found with id: " + id);
        }
        item.setId(id);
        return repository.save(item);
    }

    public void delete(int id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Dealer not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
