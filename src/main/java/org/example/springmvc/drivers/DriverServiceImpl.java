package org.example.springmvc.drivers;

import org.example.springmvc.auth.SecurityUtils;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.dto.UpdateDriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.drivers.dto.DriverDTO;
import org.example.springmvc.exceptions.DuplicateEntityException;
import org.example.springmvc.exceptions.EntityNotFoundException;
import org.example.springmvc.exceptions.ErrorMessages;
import org.example.springmvc.exceptions.UnauthorizedActionException;
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
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND_ID, userId)
                ));

        if (user.getDriver() != null) {
            throw new UnauthorizedActionException(ErrorMessages.USER_ALREADY_DRIVER);
        }

        if (driverRepository.findBySsn(dto.ssn()).isPresent()) {
            throw new DuplicateEntityException(ErrorMessages.DRIVER_SSN_EXISTS);
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
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.DRIVER_NOT_FOUND_ID, driverId)
                ));
        return DriverMapper.toDto(driver);
    }

    @Override
    public void update(UUID driverId, UpdateDriverDTO dto) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.DRIVER_NOT_FOUND_ID, driverId)
                ));

        if (!driver.getSsn().equals(dto.ssn()) && driverRepository.findBySsn(dto.ssn()).isPresent()) {
            throw new DuplicateEntityException(ErrorMessages.DRIVER_SSN_EXISTS);
        }

        DriverMapper.updateEntity(driver, dto);
    }

    @Override
    public void delete(UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.DRIVER_NOT_FOUND_ID, driverId)
                ));

        User user = driver.getUser();
        if (user != null) {
            user.setDriver(null);
            user.setRole(UserRole.APP_USER);
        }

        driverRepository.delete(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverDTO> search(Pageable pageable, DriverFilter filter) {
        UUID driverId = parseDriverId(filter.driverId());

        if (filter.driverId() != null && !filter.driverId().isBlank() && driverId == null) {
            return Page.empty(pageable);
        }

        String searchIn = normalizeSearchIn(filter.searchIn());

        return driverRepository.searchDrivers(
                wildcard(filter.q()),
                searchIn,
                wildcard(filter.fname()),
                wildcard(filter.lname()),
                wildcard(filter.ssn()),
                driverId,
                pageable
        ).map(DriverMapper::toDto);
    }

    private String wildcard(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return "%" + value.trim().toLowerCase() + "%";
    }

    private UUID parseDriverId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String normalizeSearchIn(String searchIn) {
        if (searchIn == null || searchIn.isBlank()) {
            return "all";
        }

        return switch (searchIn.toLowerCase()) {
            case "fname", "lname", "ssn" -> searchIn.toLowerCase();
            default -> "all";
        };
    }
}