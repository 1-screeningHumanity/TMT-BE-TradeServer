package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationBuyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationBuyJpaRepository extends JpaRepository<ReservationBuyEntity, Long> {

    List<ReservationBuyEntity> findAllByUuid(String uuid);
}
