package org.nextme.product_service.product.application.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.product_service.product.domain.Product;
import org.nextme.product_service.product.infrastructure.ProductRepository;
import org.nextme.product_service.product.infrastructure.client.PaymentClient;
import org.nextme.product_service.product.infrastructure.client.ReservationClient;
import org.nextme.product_service.product.presentation.PaymentInitRequest;
import org.nextme.product_service.product.presentation.PaymentInitResponse;
import org.nextme.product_service.product.presentation.ProductRequest;
import org.nextme.product_service.product.presentation.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service // Spring Bean으로 등록
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService { // 인터페이스 구현

    private final ProductRepository productRepository;
    private final ScheduleMapGenerator scheduleMapGenerator;
    private final ReservationClient reservationClient;
    private final PaymentClient paymentClient;


    // C (Create)
    @Override
    @Transactional
    public ProductResponse createProduct(UUID advisorId, ProductRequest request) {
        Product product = Product.create(advisorId, request);
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    // R (Read - 단일 조회)
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        return ProductResponse.from(product);
    }

    // R (Read - 전체 목록 조회)
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    // U (Update)
    @Override
    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found for update: " + productId));

        product.updateInfo(request);

        return ProductResponse.from(product);
    }

    // D (Delete)
    @Override
    @Transactional
    public void deleteProduct(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found for deletion: " + productId);
        }
        productRepository.deleteById(productId);
    }

    @Override
    public List<Map<String, Object>> getProductAvailableSchedules(UUID productId, List<LocalDate> dates) {
        // 1. 상품 정보 조회 (시작시간, 종료시간, 상담시간 등 설정을 가져옴)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다: " + productId));

        List<Map<String, Object>> result = new ArrayList<>();

        for (LocalDate date : dates) {
            // 2. FeignClient를 통해 예약 서비스에서 예약된 시간(LocalTime 리스트)을 가져옴
            // DB에 데이터가 없으면 빈 리스트 [] 가 반환됩니다.
            List<LocalTime> reservedTimes = reservationClient.getBookedTimes(productId, date);

            log.info("조회 날짜: {}, 예약된 시간들: {}", date, reservedTimes);

            // 3. Generator를 사용해 해당 날짜의 전체 타임 슬롯 생성
            // 이 안에서 reservedTimes.contains(currentTime) 로 예약 여부(true/false)를 판단합니다.
            List<Map<String, Object>> slots = scheduleMapGenerator.generateSlots(date, product, reservedTimes);

            // 4. API 응답 형식에 맞게 날짜와 슬롯 리스트를 맵핑
            Map<String, Object> daySchedule = new HashMap<>();
            daySchedule.put("date", date);
            daySchedule.put("slots", slots);

            result.add(daySchedule);
        }

        return result;
    }

    /**
     * 상품 구매를 위한 결제 준비 로직
     */
    public PaymentInitResponse preparePayment(UUID userId, UUID productId) {
        // 1. 상품 정보 조회 (상품명, 가격 등)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 2. 결제 서비스에 전달할 요청 객체 생성
        PaymentInitRequest request = new PaymentInitRequest(
                userId,
                product.getProductName(),
                (long) product.getPrice()
        );

        // 3. 🌟 Feign을 통해 Payment Service의 /init 호출
        return paymentClient.initPayment(request);
    }
}
