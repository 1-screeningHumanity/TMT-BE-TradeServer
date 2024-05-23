package ScreeningHumanity.TradeServer.adaptor.in.web.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class RequestVo {

    @Getter
    public static class StockBuy {

        @NotNull
        private String stockCode;
        @NotNull
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
