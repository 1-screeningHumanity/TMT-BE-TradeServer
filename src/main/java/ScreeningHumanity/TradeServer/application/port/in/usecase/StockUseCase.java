package ScreeningHumanity.TradeServer.application.port.in.usecase;

import ScreeningHumanity.TradeServer.application.port.in.dto.StockInDto;

public interface StockUseCase {

    void buyStock(StockInDto.Buy buyDto, String uuid, String accessToken);

    void saleStock(StockInDto.Sale saleDto, String uuid);
}
