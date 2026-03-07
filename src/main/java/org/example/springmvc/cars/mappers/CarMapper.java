package org.example.springmvc.cars.mappers;

import org.example.springmvc.cars.model.Car;
import org.example.springmvc.cars.model.dto.CarDTO;
import org.example.springmvc.cars.model.dto.CreateCarDTO;

public class CarMapper {

    public static CarDTO toDto(Car car) {
        return new CarDTO(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getHourlyPrice(),
                car.getLicencePlate(),
                car.getVin(),
                car.getYear()
        );
    }

    public static Car fromDto(CreateCarDTO dto) {
        return new Car(
                dto.make(),
                dto.model(),
                dto.hourlyPrice(),
                dto.licencePlate(),
                dto.vin(),
                dto.year()
        );
    }
}
