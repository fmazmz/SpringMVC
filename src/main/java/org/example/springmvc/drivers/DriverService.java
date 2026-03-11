package org.example.springmvc.drivers;

import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.dto.DriverDTO;
import org.example.springmvc.drivers.dto.UpdateDriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DriverService {

    Driver becomeDriver(UUID userId, CreateDriverDTO dto);

    List<Driver> getAll();

    Page<DriverDTO> getAllPageable(Pageable pageable);

    DriverDTO getById(UUID driverId);

    void update(UUID driverId, UpdateDriverDTO dto);

    void delete(UUID driverId);

    Page<DriverDTO> search(Pageable pageable, DriverFilter filter);
}