package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import java.util.Optional;

public interface SaveReservationStockPort {

    void saveReservationBuyStock(ReservationBuy reservationBuy);

    void saveReservationSaleStock(ReservationSale reservationSale);

    Optional<ReservationSale> deleteReservationSaleStock(Long saleId);

    Optional<ReservationBuy> deleteReservationBuyStock(Long buyId);
}
