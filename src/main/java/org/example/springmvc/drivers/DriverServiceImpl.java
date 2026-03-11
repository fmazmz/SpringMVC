package org.example.springmvc.drivers;

import org.example.springmvc.auth.SecurityUtils;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.dto.UpdateDriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.drivers.dto.DriverDTO;
import org.example.springmvc.users.model.User;
import org.example.springmvc.users.UserRepository;
import org.example.springmvc.users.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public DriverServiceImpl(DriverRepository driverRepository, UserRepository userRepository, SecurityUtils securityUtils) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    public Driver becomeDriver(UUID userId, CreateDriverDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getDriver() != null) {
            throw new IllegalStateException("User is already a driver");
        }

        if (driverRepository.findBySsn(dto.ssn()).isPresent()) {
            throw new IllegalArgumentException("Driver with this SSN already exists");
        }

        Driver driver = DriverMapper.fromDto(dto);
        driver.setUser(user);

        driverRepository.save(driver);

        user.setDriver(driver);
        user.setRole(UserRole.DRIVER);

        securityUtils.refreshAuthentication(user.getEmail());
        return driver;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Driver> getAll() {
        return driverRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverDTO> getAllPageable(Pageable pageable) {
        return driverRepository.findAll(pageable)
                .map(DriverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverDTO getById(UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        return DriverMapper.toDto(driver);
    }

    @Override
    public void update(UUID driverId, UpdateDriverDTO dto) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        if (!driver.getSsn().equals(dto.ssn()) && driverRepository.findBySsn(dto.ssn()).isPresent()) {
            throw new IllegalArgumentException("SSN already exists");
        }

        DriverMapper.updateEntity(driver, dto);
    }

    @Override
    public void delete(UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        driverRepository.delete(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverDTO> search(Pageable pageable, DriverFilter filter) {
        UUID driverId = null;
        if (filter.driverId() != null && !filter.driverId().isBlank()) {
            driverId = UUID.fromString(filter.driverId());
        }

        return driverRepository.searchDrivers(
                wildcard(filter.q()),
                wildcard(filter.fname()),
                wildcard(filter.lname()),
                wildcard(filter.ssn()),
                driverId,
                pageable
        ).map(DriverMapper::toDto);
    }

    private String wildcard(String value) {
        if (value == null || value.isBlank()) return null;
        return "%" + value.trim().toLowerCase() + "%";
    }
}