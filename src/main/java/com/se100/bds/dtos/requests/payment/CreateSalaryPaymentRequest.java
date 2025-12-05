package com.se100.bds.dtos.requests.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateSalaryPaymentRequest {
    @NotNull(message = "Agent ID is required")
    private UUID agentId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private LocalDate dueDate;
    
    private String notes;
}
