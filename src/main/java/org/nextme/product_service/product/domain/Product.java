package org.nextme.product_service.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nextme.common.jpa.BaseEntity;
import org.nextme.product_service.product.presentation.ProductRequest;

import java.math.BigDecimal;
import java.rmi.server.UID;
import java.util.UUID;

@Entity
@Table(name = "p_product")
@Getter
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    private UUID advisorId;

    private String productName;

    private String description;

    private String category;

    private int durationMin;

    private int restTime;

    private int workingHours;

    private int startTime;
    private int endTime;

    private boolean isReserved;

    private int price;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;


    @Builder
    public Product(UUID advisorId, String productName, String description, String category, int durationMin, int restTime, int workingHours, int startTime, int  endTime, boolean isReserved, int price, DayOfWeek dayOfWeek) {
        this.advisorId = advisorId;
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.durationMin = durationMin;
        this.restTime = restTime;
        this.workingHours = workingHours;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isReserved = isReserved;
        this.price = price;
        this.dayOfWeek = dayOfWeek;
    }

    public static Product create(UUID advisorId, ProductRequest request) {
        // 필수 필드 검증 (예시)
        if (request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }

        return new Product(
                advisorId,
                request.getProductName(),
                request.getDescription(),
                request.getCategory(),
                request.getDurationMin(),
                request.getRestTime(),
                request.getWorkingHours(),
                request.getStartTime(),
                request.getEndTime(),
                false, // 초기에는 예약되지 않은 상태로 설정
                request.getPrice(),
                request.getDayOfWeek()
        );
    }

    public void updateInfo(ProductRequest request) {
        // 가격 변경 로직 (예: 가격은 0보다 커야 함)
        if (request.getPrice() <= 0) {
            throw new IllegalArgumentException("New price must be positive.");
        }

        this.productName = request.getProductName();
        this.description = request.getDescription();
        this.category = request.getCategory();
        this.durationMin = request.getDurationMin();
        this.restTime = request.getRestTime();
        this.workingHours = request.getWorkingHours();
        this.price = request.getPrice();
        this.dayOfWeek = request.getDayOfWeek();
    }

    public void changePrice(int newPrice) {
        if (newPrice <= 0) {
            throw new IllegalArgumentException("변경할 가격은 0보다 커야 합니다.");
        }
        this.price = newPrice; // 오직 이 메서드를 통해서만 가격이 변경됨
    }

    public void reserve() {
        if (this.isReserved) {
            throw new IllegalStateException("이미 예약된 상품입니다.");
        }
        this.isReserved = true;
    }

}
