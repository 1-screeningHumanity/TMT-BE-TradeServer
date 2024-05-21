package ScreeningHumanity.TradeServer.application.port.in.usecase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public interface StockUseCase {

    void BuyStock(StockBuyDto stockBuyDto, String uuid);

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    class StockBuyDto {

        private Long stockCode;
        private Long price;
        private Long amount;
        private String uuid;
    }
}
