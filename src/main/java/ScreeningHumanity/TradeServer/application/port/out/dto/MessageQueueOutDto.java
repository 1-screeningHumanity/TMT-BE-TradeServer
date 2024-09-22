package ScreeningHumanity.TradeServer.application.port.out.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class MessageQueueOutDto {

    /**
     * 일반 매수 후, 금액 차감 요청 Dto
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class BuyDto {

        @NotNull
        private String uuid;
        @NotNull
        private Long price;
    }

    @Getter
    @Builder
    public static class SaleDto {

        @NotNull
        private String uuid;
        @NotNull
        private Long price;

        @Builder
        private SaleDto(String uuid, Long price) {
            this.uuid = uuid;
            this.price = price;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReservationBuyCancelDto {

        @NotNull
        private String uuid;
        @NotNull
        private Long price;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TradeStockNotificationDto {

        @NotNull
        private String title;

        @NotNull
        private String body;

        @NotNull
        private String uuid;

        @NotNull
        private String notificationLogTime;
    }
}
