package com.se100.bds.services.domains.payment.webhook.impl;

import com.se100.bds.models.entities.contract.Payment;
import com.se100.bds.services.domains.payment.webhook.PaymentGatewayWebhookEvent;
import com.se100.bds.services.domains.payment.webhook.PaymentSucceededSideEffectHandler;
import com.se100.bds.utils.Constants.PaymentTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Side effects for SERVICE_FEE payments.
 *
 * TODO: Implement business behavior (e.g., mark property as publishable / visible on public listing)
 * once rules are confirmed.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceFeePaymentSucceededHandler implements PaymentSucceededSideEffectHandler {

    @Override
    public boolean supports(Payment payment) {
        return payment != null && payment.getPaymentType() == PaymentTypeEnum.SERVICE_FEE;
    }

    @Override
    public void handle(Payment payment, PaymentGatewayWebhookEvent event) {
        // Scaffold only.
        log.info("SERVICE_FEE succeeded for paymentId={}, contractId={}, propertyId={}, gatewayEventId={}",
                payment.getId(),
                payment.getContract() != null ? payment.getContract().getId() : null,
                payment.getProperty() != null ? payment.getProperty().getId() : null,
                event != null ? event.getExternalEventId() : null);

        // TODO: apply domain state transitions for service fee success.
    }
}
