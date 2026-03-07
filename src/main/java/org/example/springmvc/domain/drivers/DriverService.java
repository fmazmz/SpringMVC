package org.example.springmvc.domain.drivers;

import org.example.springmvc.domain.drivers.model.dto.CreateDriverDTO;
import org.example.springmvc.domain.drivers.model.Driver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {
    private final DriverRepository repository;

    public DriverService(DriverRepository repository) {
        this.repository = repository;
    }

    public void create(CreateDriverDTO dto) {
        Driver driver = DriverMapper.fromDto(dto);
        repository.save(driver);
    }

    public List<Driver> getAll() {
        return repository.findAll();
    }
}