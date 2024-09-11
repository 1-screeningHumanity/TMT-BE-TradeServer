package ScreeningHumanity.TradeServer.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class StockInDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Buy {

        private String stockCode;
        private Long price;
        private Long amount;
        private String uuid;
        private String stockName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Sale {

        private String stockCode;
        private Long price;
        private Long amount;
        private String uuid;
        private String stockName;
    }
}
