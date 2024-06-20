package ScreeningHumanity.TradeServer.adaptor.in.web.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class RequestVo {

    @Getter
    public static class StockBuy {

        @NotNull
        private String stockCode;
        @NotNull
        @Min(value = 100L, message = "매수 가격은 100원 이상부터 입니다.")
        private Long price;
        @NotNull
        private Long amount;
        @NotNull
        private String stockName;
    }

    @Getter
    public static class StockSale {

        @NotNull
        private String stockCode;
        @NotNull
        private Long price;
        @NotNull
        private Long amount;
        @NotNull
        private String stockName;
    }
}
