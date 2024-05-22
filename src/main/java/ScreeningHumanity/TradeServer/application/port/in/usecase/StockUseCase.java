package ScreeningHumanity.TradeServer.application.port.in.usecase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public interface StockUseCase {

    void BuyStock(StockBuySaleDto stockBuyDto, String uuid);
    void SaleStock(StockBuySaleDto stockSaleDto, String uuid);

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    class StockBuySaleDto {

        private Long stockCode;
        private Long price;
        private Long amount;
        private String uuid;
    }
}
