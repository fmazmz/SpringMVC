package org.example.springmvc.drivers;

import org.example.springmvc.cars.mappers.CarMapper;

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
