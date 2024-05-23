package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockLogJpaRepository extends JpaRepository<StockLogEntity, Long>, StockLogQueryDslRepository {

}
