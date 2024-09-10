package ScreeningHumanity.TradeServer.application.port.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class RequestDto {

    @Getter
    public static class StockBuy {

        @NotNull
        private String stockCode;

        @NotNull
        @Min(value = 100L, message = "매수 가격은 100원 이상부터 입니다.")
        private Long price;

        @NotNull
        @Min(value = 1L, message = "매수 최소 수량은 1원 이상부터 입니다.")
        private Long amount;

        @NotNull
        private String stockName;
    }

    @Getter
    public static class StockSale {

        @NotNull
        private String stockCode;

        @NotNull
        @Min(value = 100L, message = "매도 가격은 100원 이상부터 입니다.")
        private Long price;

        @NotNull
        @Min(value = 1L, message = "매도 최소 수량은 1원 이상부터 입니다.")
        private Long amount;

        @NotNull
        private String stockName;
    }

    @Getter
    public static class WonInfo {
        @NotNull
        private Long won;
    }
}
