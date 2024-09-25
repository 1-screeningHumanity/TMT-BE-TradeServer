package ScreeningHumanity.TradeServer.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class StockInDto {

    @Getter
    @NoArgsConstructor
    public static class Buy {

        private String stockCode;
        private Long price;
        private Long amount;
        private String uuid;
        private String stockName;

        @Builder
        private Buy(String stockCode, Long price, Long amount, String uuid, String stockName) {
            this.stockCode = stockCode;
            this.price = price;
            this.amount = amount;
            this.uuid = uuid;
            this.stockName = stockName;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Sale {

        private String stockCode;
        private Long price;
        private Long amount;
        private String uuid;
        private String stockName;

        @Builder
        private Sale(String stockCode, Long price, Long amount, String uuid, String stockName) {
            this.stockCode = stockCode;
            this.price = price;
            this.amount = amount;
            this.uuid = uuid;
            this.stockName = stockName;
        }
    }
}
