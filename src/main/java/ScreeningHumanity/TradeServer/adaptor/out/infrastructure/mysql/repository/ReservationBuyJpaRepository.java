package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationBuyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationBuyJpaRepository extends JpaRepository<ReservationBuyEntity, Long> {
}
