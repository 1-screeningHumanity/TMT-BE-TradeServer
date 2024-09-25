package ScreeningHumanity.TradeServer.application.port.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RequestDto {

    @Getter
    @NoArgsConstructor
    public static class StockBuy {

        @NotBlank(message = "주식 코드는 필수입니다.")
        private String stockCode;

        @Min(value = 100L, message = "매수 가격은 100원 이상부터 입니다.")
        private Long price;

        @Positive(message = "매수 최소 수량은 1개 이상부터 입니다.")
        private Long amount;

        @NotBlank(message = "주식 이름은 필수입니다.")
        private String stockName;

        @Builder
        private StockBuy(String stockCode, Long price, Long amount, String stockName) {
            this.stockCode = stockCode;
            this.price = price;
            this.amount = amount;
            this.stockName = stockName;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class StockReservationBuy {

        @NotBlank(message = "주식 코드는 필수입니다.")
        private String stockCode;

        @Min(value = 100L, message = "매수 가격은 100원 이상부터 입니다.")
        private Long price;

        @Positive(message = "예약 매수 최소 수량은 1개 이상부터 입니다.")
        private Long amount;

        @NotBlank(message = "주식 이름은 필수입니다.")
        private String stockName;

        @Builder
        private StockReservationBuy(String stockCode, Long price, Long amount, String stockName) {
            this.stockCode = stockCode;
            this.price = price;
            this.amount = amount;
            this.stockName = stockName;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class StockSale {

        @NotBlank(message = "주식 코드는 필수입니다.")
        private String stockCode;

        @Min(value = 100L, message = "매도 가격은 100원 이상부터 입니다.")
        private Long price;

        @Positive(message = "매도 최소 수량은 1개 이상부터 입니다.")
        private Long amount;

        @NotBlank(message = "주식 이름은 필수입니다.")
        private String stockName;

        @Builder
        private StockSale(String stockCode, Long price, Long amount, String stockName) {
            this.stockCode = stockCode;
            this.price = price;
            this.amount = amount;
            this.stockName = stockName;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class StockReservationSale {

        @NotBlank(message = "주식 코드는 필수입니다.")
        private String stockCode;

        @Min(value = 100L, message = "매도 가격은 100원 이상부터 입니다.")
        private Long price;

        @Positive(message = "예약 매도 최소 수량은 1개 이상부터 입니다.")
        private Long amount;

        @NotBlank(message = "주식 이름은 필수입니다.")
        private String stockName;

        @Builder
        private StockReservationSale(String stockCode, Long price, Long amount, String stockName) {
            this.stockCode = stockCode;
            this.price = price;
            this.amount = amount;
            this.stockName = stockName;
        }
    }

    @Getter
    public static class WonInfo {
        @NotNull
        private Long won;

        @Builder
        private WonInfo(Long won) {
            this.won = won;
        }
    }

    @Getter
    public static class RealTimeStockInfo {
        public String stockCode;
        public Long price;
        public String date;
    }
}
