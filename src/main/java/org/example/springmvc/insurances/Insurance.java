package org.example.springmvc.insurances;

import java.math.BigDecimal;

public interface Insurance {
    BigDecimal getPrice(InsuranceType type);
}
