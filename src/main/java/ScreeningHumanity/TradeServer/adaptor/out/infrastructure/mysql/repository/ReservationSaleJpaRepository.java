package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationSaleEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSaleJpaRepository extends JpaRepository<ReservationSaleEntity, Long> {

    List<ReservationSaleEntity> findAllByUuid(String uuid);
}
