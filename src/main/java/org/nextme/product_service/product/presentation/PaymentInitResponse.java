package org.nextme.product_service.product.presentation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class PaymentInitResponse {
    private UUID orderId;
    private long amount;
    private String clientKey;

    public PaymentInitResponse(UUID orderId, long amount, String clientKey) {
        this.orderId = orderId;
        this.amount = amount;
        this.clientKey = clientKey;
    }
}
