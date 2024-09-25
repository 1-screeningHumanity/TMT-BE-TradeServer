package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.MemberStockEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface MemberStockJpaRepository extends JpaRepository<MemberStockEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<MemberStockEntity> findAllByUuidAndStockCode(String uuid, String stockCode);
}
