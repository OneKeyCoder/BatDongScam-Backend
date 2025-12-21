package com.se100.bds.services.payment;

import com.se100.bds.services.payment.dto.CreatePaymentSessionRequest;
import com.se100.bds.services.payment.dto.CreatePaymentSessionResponse;

public interface PaymentGatewayService {
    CreatePaymentSessionResponse createPaymentSession(CreatePaymentSessionRequest request, String idempotencyKey);
}
