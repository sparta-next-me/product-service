package org.nextme.product_service.product.presentation;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentInitRequest {
    private UUID userId;
    private String productName;
    private long amount;

    public PaymentInitRequest(UUID userId, String productName, long price) {
        this.userId = userId;
        this.productName = productName;
        this.amount = price;
    }
}
