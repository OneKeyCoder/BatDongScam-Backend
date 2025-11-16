package com.se100.bds.services.payment;

import com.se100.bds.models.entities.contract.Payment;

import java.util.Map;

public interface PaymentStrategy {
    Payment pay(Map<String, Object> paymentParams);
    Payment executePayment(Map<String, Object> paymentParams);
    Payment payoutPayment(Map<String, Object> paymentParams);
}
