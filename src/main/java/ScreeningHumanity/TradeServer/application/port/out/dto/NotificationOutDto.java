package ScreeningHumanity.TradeServer.application.port.out.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class NotificationOutDto {

    /**
     * 일반 매수 후, 금액 차감 요청 Dto
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class BuyDto{
        @NotNull
        private String uuid;
        @NotNull
        private Long price;
    }

}
