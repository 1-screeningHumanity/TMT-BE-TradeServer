package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.ReservationBuy;

public interface SaveReservationStockPort {
    void SaveReservationBuyStock(ReservationBuy reservationBuy);
}
