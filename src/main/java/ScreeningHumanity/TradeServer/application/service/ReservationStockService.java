package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadReservationStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveReservationStockPort;
import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationStockService implements ReservationStockUseCase {
    private final LoadReservationStockPort loadReservationStockPort;
    private final SaveReservationStockPort saveReservationStockPort;


    @Override
    public void BuyStock(StockBuySaleDto receiveStockBuyDto, String uuid) {

        ReservationBuy reservationBuy = createReservationStock(receiveStockBuyDto, uuid);

        saveReservationStockPort.SaveReservationBuyStock(reservationBuy);
    }

    private ReservationBuy createReservationStock(StockBuySaleDto receiveStockBuyDto, String uuid){
        return ReservationBuy
                .builder()
                .uuid(uuid)
                .price(receiveStockBuyDto.getPrice())
                .amount(receiveStockBuyDto.getAmount())
                .stockCode(receiveStockBuyDto.getStockCode())
                .stockName(receiveStockBuyDto.getStockName())
                .build();
    }
}
