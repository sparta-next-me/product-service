package org.nextme.product_service.product.presentation;

import lombok.RequiredArgsConstructor;
import org.nextme.common.security.UserPrincipal;
import org.nextme.product_service.product.application.service.ProductService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products") // API 기본 경로 설정
@RequiredArgsConstructor // final 필드(productService)를 포함하는 생성자를 자동 생성
public class ProductController {

    // Service 인터페이스를 주입받음으로써, 구현체(ServiceImpl)와 느슨하게 결합됨
    private final ProductService productService;

    // 실제 환경에서는 인증/인가 로직을 통해 요청한 어드바이저의 ID를 가져와야 합니다.
    private final UUID SAMPLE_ADVISOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    // 1. C (Create) : 새 상품 등록
    // POST /api/v1/products
    @PreAuthorize("hasRole('ADVISOR')")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request,
        @AuthenticationPrincipal UserPrincipal principal)
    {
        UUID userId = UUID.fromString(principal.userId());
        ProductResponse response = productService.createProduct(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // HTTP 201 Created
    }

    // 2. R (Read - 단일 조회) : 특정 상품 상세 조회
    // GET /api/v1/products/{productId}
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(response); // HTTP 200 OK
    }

    // 3. R (Read - 전체 목록 조회) : 전체 상품 목록 조회
    // GET /api/v1/products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> response = productService.getAllProducts();
        return ResponseEntity.ok(response); // HTTP 200 OK
    }

    // 4. U (Update) : 상품 정보 수정
    // PUT /api/v1/products/{productId}
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID productId,
                                                         @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response); // HTTP 200 OK
    }

    // 5. D (Delete) : 상품 삭제
    // DELETE /api/v1/products/{productId}
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }

    /**
     * 특정 상품의 예약 가능한 스케줄 목록을 조회합니다.
     * * @param productId 조회할 상품의 고유 ID (Path Variable)
     * @param dates 조회할 날짜 목록 (Query Parameter, 예: ?dates=2025-12-17,2025-12-18)
     * @return 날짜별 시간 슬롯 정보 (List<Map<String, Object>>)
     */
    @GetMapping("/{productId}/schedules")
    public ResponseEntity<List<Map<String, Object>>> getProductSchedules(
            @PathVariable UUID productId,
            @RequestParam(name = "dates")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            List<LocalDate> dates) {

        // 2. Service 계층의 비즈니스 로직 호출
        List<Map<String, Object>> schedules = productService.getProductAvailableSchedules(productId, dates);

        // 3. 결과를 HTTP 200 OK와 함께 반환
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/payment-init")
    public ResponseEntity<PaymentInitResponse> initPayment(
            @RequestBody ProductPaymentRequest request) {

        // 1. 서비스 호출 (내부적으로 Payment 서비스와 Feign 통신을 수행함)
        // userId는 테스트를 위해 임의의 UUID를 넣거나 토큰에서 추출하세요.
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        PaymentInitResponse response = productService.preparePayment(
                userId,
                request.productId()
        );

        // 2. 완성된 결제 준비 데이터(orderId, amount 등)를 프론트로 응답
        return ResponseEntity.ok(response);
    }
}
