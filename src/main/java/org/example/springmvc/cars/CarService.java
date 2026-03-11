package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.dto.UpdateCarDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CarService {

    void create(CreateCarDTO dto);

    Page<CarDTO> search(Pageable pageable, CarFilter filter);

    List<CarDTO> findAvailable(Instant startTime, Instant endTime);

    CarDTO getById(UUID id);

    void update(UUID id, UpdateCarDTO dto);

    void delete(UUID id);
}