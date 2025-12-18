package org.nextme.product_service.product.application.service;

import org.nextme.product_service.product.domain.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleMapGenerator {

    /**
     * 🌟 ProductServiceImpl에서 호출하는 핵심 메서드
     */
    public List<Map<String, Object>> generateSlots(LocalDate date, Product product, List<LocalTime> reservedTimes) {
        List<Map<String, Object>> slots = new ArrayList<>();

        // 1. 상품의 운영 설정값 가져오기 (int -> LocalTime 변환)
        LocalTime currentTime = convertIntToTime(product.getStartTime());
        LocalTime dayEnd = convertIntToTime(product.getEndTime());
        int duration = product.getDurationMin();
        int rest = product.getRestTime();

        // 2. 운영 종료 시간 전까지 슬롯 생성 루프
        while (!currentTime.plusMinutes(duration).isAfter(dayEnd)) {
            LocalTime slotEndTime = currentTime.plusMinutes(duration);

            Map<String, Object> slot = new HashMap<>();

            // 시간 범위 포맷팅 (예: "14:00~15:00")
            slot.put("timeRange", currentTime.toString() + "~" + slotEndTime.toString());
            slot.put("restDurationMin", rest);

            // 🌟 예약 여부 확인: 예약 서비스에서 받아온 목록에 포함되어 있는지 체크
            boolean isReserved = reservedTimes.contains(currentTime);
            slot.put("isReserved", isReserved);

            slots.add(slot);

            // 3. 다음 슬롯 시작 시간 계산 (종료 시간 + 휴식 시간)
            currentTime = slotEndTime.plusMinutes(rest);
        }

        return slots;
    }

    /**
     * 숫자로 된 시간(1400)을 LocalTime(14:00)으로 변환하는 헬퍼 메서드
     */
    private LocalTime convertIntToTime(int time) {
        int hour = time / 100;
        int minute = time % 100;
        return LocalTime.of(hour, minute);
    }
}
