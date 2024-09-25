package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationSaleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationSaleJpaRepository extends JpaRepository<ReservationSaleEntity, Long> {

    List<ReservationSaleEntity> findAllByUuid(String uuid);

    List<ReservationSaleEntity> findByStockCodeAndPrice(String stockCode, Long price);

    @Query("select sum(r.amount) from ReservationSaleEntity r where r.stockCode = :stockCode and r.uuid = :uuid")
    Optional<Long> findReservedAmountByStockCode(@Param("stockCode") String stockCode, @Param("uuid") String uuid);
}
