package org.example.springmvc.insurances;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CarInsurance implements Insurance{
    private static final BigDecimal BASIC_PRICE = new BigDecimal("79.0");
    private static final BigDecimal PREMIUM_PRICE = new BigDecimal("129.0");
    private static final BigDecimal FULL_COVERAGE_PRICE = new BigDecimal("199.0");

    @Override
    public BigDecimal getPrice(InsuranceType type) {
        return switch (type) {
            case BASIC -> BASIC_PRICE;
            case PREMIUM -> PREMIUM_PRICE;
            case FULL_COVERAGE -> FULL_COVERAGE_PRICE;
        };
    }
}
