package ScreeningHumanity.TradeServer.adaptor.in.web.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

public class RequestVo {

    @Getter
    public static class StockBuy {

        @NotNull
        private Long stockCode;
        @NotNull
        private Long price;
        @NotNull
        private Long amount;
    }

    @Getter
    public static class StockSale {

        @NotNull
        private Long stockCode;
        @NotNull
        private Long price;
        @NotNull
        private Long amount;
    }
}
