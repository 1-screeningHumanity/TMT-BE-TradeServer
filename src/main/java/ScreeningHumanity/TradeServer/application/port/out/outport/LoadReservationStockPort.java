package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import java.util.List;
import java.util.Optional;

public interface LoadReservationStockPort {

    List<ReservationBuy> loadReservationBuy(String uuid);

    List<ReservationSale> loadReservationSale(String uuid);

    List<ReservationBuy> findMatchBuyStock(String stockCode, Long nowPrice);

    List<ReservationSale> findMatchSaleStock(String stockCode, Long nowPrice);

    Optional<Long> countSaleStockByStockCode(String stockCode, String uuid);
}
