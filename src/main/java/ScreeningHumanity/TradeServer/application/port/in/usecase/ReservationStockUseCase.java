package ScreeningHumanity.TradeServer.application.port.in.usecase;

import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationLogOutDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public interface ReservationStockUseCase {

    void BuyStock(ReservationStockUseCase.StockBuySaleDto stockBuyDto, String uuid);

    void SaleStock(ReservationStockUseCase.StockBuySaleDto stockBuyDto, String uuid);

    List<ReservationLogOutDto> BuySaleLog(String uuid);

    void DeleteSaleStock(Long saleId);

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
