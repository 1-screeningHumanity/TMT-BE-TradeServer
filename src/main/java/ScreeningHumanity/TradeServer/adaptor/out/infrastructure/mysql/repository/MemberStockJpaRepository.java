package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.MemberStockEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberStockJpaRepository extends JpaRepository<MemberStockEntity, Long> {

    Optional<MemberStockEntity> findAllByUuidAndStockCode(String uuid, Long stockCode);
}
