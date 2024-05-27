package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationBuyEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.ReservationBuyJpaRepository;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadReservationStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveReservationStockPort;
import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberReservationStockAdaptor implements SaveReservationStockPort,
        LoadReservationStockPort {

    private final ReservationBuyJpaRepository reservationBuyJpaRepository;

    @Override
    public void SaveReservationBuyStock(ReservationBuy reservationBuy) {
        reservationBuyJpaRepository.save(ReservationBuyEntity.toEntityFrom(reservationBuy));
    }
}
