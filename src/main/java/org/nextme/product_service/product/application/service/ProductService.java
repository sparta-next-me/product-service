package org.nextme.product_service.product.application.service;

import org.nextme.product_service.product.presentation.PaymentInitResponse;
import org.nextme.product_service.product.presentation.ProductRequest;
import org.nextme.product_service.product.presentation.ProductResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {

    // C (Create)
    ProductResponse createProduct(UUID advisorId, ProductRequest request);

    // R (Read - 단일 조회)
    ProductResponse getProductById(UUID productId);

    // R (Read - 전체 목록 조회)
    List<ProductResponse> getAllProducts();

    // U (Update)
    ProductResponse updateProduct(UUID productId, ProductRequest request);

    // D (Delete)
    void deleteProduct(UUID productId);

    List<Map<String, Object>> getProductAvailableSchedules(UUID productId, List<LocalDate> dates);

    PaymentInitResponse preparePayment(UUID userId, UUID productId);

}
