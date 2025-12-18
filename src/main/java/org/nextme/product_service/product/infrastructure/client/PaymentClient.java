package org.nextme.product_service.product.infrastructure.client;

import org.nextme.product_service.product.presentation.PaymentInitRequest;
import org.nextme.product_service.product.presentation.PaymentInitResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://34.50.7.8:11113")
public interface PaymentClient {

    @PostMapping("/v1/payments/init")
    PaymentInitResponse initPayment(
            // 인증 토큰 전달용
            @RequestBody PaymentInitRequest request
    );
}
