package org.example.springmvc.drivers;

import org.springframework.stereotype.Service;

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
}
