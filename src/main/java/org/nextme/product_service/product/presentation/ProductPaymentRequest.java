package org.nextme.product_service.product.presentation;

import lombok.Getter;

import java.util.UUID;

public record ProductPaymentRequest(
        UUID productId
) {}
