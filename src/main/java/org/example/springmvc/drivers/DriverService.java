package org.example.springmvc.drivers;

import org.example.springmvc.auth.SecurityUtils;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.dto.DriverFilter;
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
import org.example.springmvc.utils.SearchUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DriverService {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository, SecurityUtils securityUtils) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

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

    @Transactional(readOnly = true)
    public List<Driver> getAll() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<DriverDTO> getAllPageable(Pageable pageable) {
        return driverRepository.findAll(pageable)
                .map(DriverMapper::toDto);
    }

    @Transactional(readOnly = true)
    public DriverDTO getById(UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.DRIVER_NOT_FOUND_ID, driverId)
                ));
        return DriverMapper.toDto(driver);
    }

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

    @Transactional(readOnly = true)
    public Page<DriverDTO> search(Pageable pageable, DriverFilter filter) {
        return driverRepository.searchDrivers(
                SearchUtils.toWildcardPattern(filter.q()),
                SearchUtils.toWildcardPattern(filter.fname()),
                SearchUtils.toWildcardPattern(filter.lname()),
                SearchUtils.toWildcardPattern(filter.ssn()),
                filter.driverId(),
                pageable
        ).map(DriverMapper::toDto);
    }
}

