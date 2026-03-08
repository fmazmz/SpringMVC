package org.example.springmvc.drivers.service;

import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.mapper.DriverMapper;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.drivers.dto.DriverDTO;
import org.example.springmvc.drivers.repository.DriverRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<DriverDTO> getAllPageable(Pageable pageable) {
        return repository.findAll(pageable)
                .map(DriverMapper::toDto);
    }
}