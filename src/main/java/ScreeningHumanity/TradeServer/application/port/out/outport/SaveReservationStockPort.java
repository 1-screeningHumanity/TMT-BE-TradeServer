package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import java.util.List;

public interface SaveReservationStockPort {

    ReservationBuy SaveReservationBuyStock(ReservationBuy reservationBuy);

    void SaveReservationSaleStock(ReservationSale reservationSale);

    ReservationSale DeleteReservationSaleStock(Long saleId);

    ReservationBuy DeleteReservationBuyStock(Long buyId);

    void concludeBuyStock(List<ReservationBuy> buyList);

    void concludeSaleStock(List<ReservationSale> buyList);
}
