package ScreeningHumanity.TradeServer.application.port.in.usecase;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public interface StockUseCase {

    void BuyStock(StockBuySaleDto stockBuyDto, String uuid, String accessToken);
    void SaleStock(StockBuySaleDto stockSaleDto, String uuid);

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    class StockBuySaleDto {

        private String stockCode;
        private Long price;
        private Long amount;
        private String uuid;
        private String stockName;
    }
}
