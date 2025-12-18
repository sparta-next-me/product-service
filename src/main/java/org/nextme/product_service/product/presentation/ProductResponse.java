package org.nextme.product_service.product.presentation;

import lombok.Builder;
import lombok.Getter;
import org.nextme.product_service.product.domain.DayOfWeek;
import org.nextme.product_service.product.domain.Product;

import java.util.UUID;

@Getter
@Builder // 응답 객체 생성을 쉽게 하기 위해 Builder 패턴 사용
public class ProductResponse {
    private final UUID productId;
    private final UUID advisorId;
    private final String productName;
    private final String description;
    private final String category;
    private final int durationMin;
    private final int restTime;
    private final int workingHours;
    private final int startTime;
    private final int endTime;
    private final boolean isReserved;
    private final int price;
    private final DayOfWeek dayOfWeek;

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .advisorId(product.getAdvisorId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .category(product.getCategory())
                .durationMin(product.getDurationMin())
                .restTime(product.getRestTime())
                .workingHours(product.getWorkingHours())
                .startTime(product.getStartTime())
                .endTime(product.getEndTime())
                .isReserved(product.isReserved())
                .price(product.getPrice())
                .dayOfWeek(product.getDayOfWeek())
                .build();
    }
}
