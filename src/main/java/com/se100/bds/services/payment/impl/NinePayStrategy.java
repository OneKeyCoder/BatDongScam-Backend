package com.se100.bds.services.payment.impl;

import com.se100.bds.models.entities.contract.Payment;
import com.se100.bds.services.payment.PaymentStrategy;
import com.se100.bds.services.payment.gateway.NinePayGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class NinePayStrategy implements PaymentStrategy {

    /// Handling logic and execute internal business logic

    private final NinePayGateway gateway;

    @Override
    public Payment pay(Map<String, Object> paymentParams) {
        return null;
    }

    @Override
    public Payment executePayment(Map<String, Object> paymentParams) {
        return null;
    }

    @Override
    public Payment payoutPayment(Map<String, Object> paymentParams) {
        return null;
    }
}
