package org.example.springmvc.drivers;

import org.example.springmvc.cars.CarMapper;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.dto.UpdateDriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.drivers.dto.DriverDTO;

public class DriverMapper {

    public static Driver fromDto(CreateDriverDTO dto) {
        return new Driver(
                dto.fname(),
                dto.lname(),
                dto.ssn()
        );
    }

    public static DriverDTO toDto(Driver driver) {
        return new DriverDTO(
                driver.getId(),
                driver.getFname(),
                driver.getLname(),
                driver.getSsn(),
                driver.getCars()
                        .stream()
                        .map(CarMapper::toDto)
                        .toList()
        );
    }

    public static void updateEntity(Driver driver, UpdateDriverDTO dto) {
        driver.setFname(dto.fname());
        driver.setLname(dto.lname());
        driver.setSsn(dto.ssn());
    }
}