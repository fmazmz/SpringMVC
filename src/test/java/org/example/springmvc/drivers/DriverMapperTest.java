package org.example.springmvc.drivers;

import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.dto.DriverDTO;
import org.example.springmvc.drivers.dto.UpdateDriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DriverMapperTest {

    @Test
    void fromDto_shouldMapAllFields() {
        CreateDriverDTO dto = new CreateDriverDTO("Erik", "Svensson", "1990010112");

        Driver driver = DriverMapper.fromDto(dto);

        assertEquals("Erik", driver.getFname());
        assertEquals("Svensson", driver.getLname());
        assertEquals("1990010112", driver.getSsn());
        assertNull(driver.getId());
    }

    @Test
    void toDto_shouldMapAllFields() {
        Driver driver = new Driver("Erik", "Svensson", "1990010112");
        driver.setId(UUID.randomUUID());

        DriverDTO dto = DriverMapper.toDto(driver);

        assertEquals(driver.getId(), dto.id());
        assertEquals("Erik", dto.fname());
        assertEquals("Svensson", dto.lname());
        assertEquals("1990010112", dto.ssn());
        assertNotNull(dto.cars());
        assertTrue(dto.cars().isEmpty());
    }

    @Test
    void updateEntity_shouldUpdateAllFields() {
        Driver driver = new Driver("Erik", "Svensson", "1990010112");
        UpdateDriverDTO dto = new UpdateDriverDTO("Anna", "Karlsson", "1985050534");

        DriverMapper.updateEntity(driver, dto);

        assertEquals("Anna", driver.getFname());
        assertEquals("Karlsson", driver.getLname());
        assertEquals("1985050534", driver.getSsn());
    }
}

