package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import java.util.List;

public interface SaveReservationStockPort {

    void SaveReservationBuyStock(ReservationBuy reservationBuy);

    void SaveReservationSaleStock(ReservationSale reservationSale);

    void DeleteReservationSaleStock(Long saleId);

    void DeleteReservationBuyStock(Long saleId);

    void concludeBuyStock(List<ReservationBuy> buyList);

    void concludeSaleStock(List<ReservationSale> buyList);
}
