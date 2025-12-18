package org.nextme.product_service.product.presentation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nextme.product_service.product.domain.DayOfWeek;

@Getter
@NoArgsConstructor // JSON 역직렬화를 위한 기본 생성자
public class ProductRequest {
    // AdvisorId는 서비스 계층에서 추가하거나, 요청 주체에서 얻으므로 DTO에서는 제외 가능
    private String productName;
    private String description;
    private String category;
    private int durationMin;
    private int restTime;
    private int workingHours;
    private int startTime;
    private int endTime;
    private int price;
    private DayOfWeek dayOfWeek;
}
