package com.se100.bds.services.payment.impl;

import com.se100.bds.models.entities.contract.Payment;
import com.se100.bds.services.payment.PaymentStrategy;

import java.util.Map;

public class NinePayStrategy implements PaymentStrategy {
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
