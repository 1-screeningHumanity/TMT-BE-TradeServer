package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationBuyEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationSaleEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.ReservationBuyJpaRepository;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.ReservationSaleJpaRepository;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadReservationStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveReservationStockPort;
import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberReservationStockAdaptor
        implements SaveReservationStockPort, LoadReservationStockPort {

    private final ReservationBuyJpaRepository reservationBuyJpaRepository;
    private final ReservationSaleJpaRepository reservationSaleJpaRepository;

    @Override
    public void SaveReservationBuyStock(ReservationBuy reservationBuy) {
        reservationBuyJpaRepository.save(ReservationBuyEntity.toEntityFrom(reservationBuy));
    }

    @Override
    public void SaveReservationSaleStock(ReservationSale reservationSale) {
        reservationSaleJpaRepository.save(ReservationSaleEntity.toEntityFrom(reservationSale));
    }
}
