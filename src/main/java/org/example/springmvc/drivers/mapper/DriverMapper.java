package org.example.springmvc.drivers.mapper;

import org.example.springmvc.cars.mapper.CarMapper;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.drivers.dto.DriverDTO;

public class DriverMapper {

    public static Driver fromDto(CreateDriverDTO dto) {
        return new Driver(
                dto.email(),
                dto.fname(),
                dto.lname(),
                dto.ssn()
        );
    }

    public static DriverDTO toDto(Driver driver) {
        return new DriverDTO(
                driver.getId(),
                driver.getEmail(),
                driver.getFname(),
                driver.getLname(),
                driver.getSsn(),
                driver.getCars()
                        .stream()
                        .map(CarMapper::toDto)
                        .toList()
        );
    }
}
