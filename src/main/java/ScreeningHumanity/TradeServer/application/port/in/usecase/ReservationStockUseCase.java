package ScreeningHumanity.TradeServer.application.port.in.usecase;

import ScreeningHumanity.TradeServer.application.port.in.dto.ReservationStockInDto;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationStockOutDto;
import java.util.List;

public interface ReservationStockUseCase {

    void buyStock(ReservationStockInDto.Buy dto, String uuid, String accessToken);

    void saleStock(ReservationStockInDto.Sale dto, String uuid);

    void doReservationStock(ReservationStockInDto.RealTimeStockInfo dto);

    List<ReservationStockOutDto.Logs> buySaleLog(String uuid);

    void cancelReservationSaleStock(Long saleId, boolean messageFlag);

    void cancelReservationBuyStock(Long saleId, boolean messageFlag);
}
