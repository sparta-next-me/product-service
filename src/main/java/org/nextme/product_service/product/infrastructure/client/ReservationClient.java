package org.nextme.product_service.product.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "reservation-service", url = "http://34.50.7.8:11114")
public interface ReservationClient {

    @GetMapping("/v1/reservations/booked-times")
    List<LocalTime> getBookedTimes(
            @RequestParam("productId") UUID productId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );
}
