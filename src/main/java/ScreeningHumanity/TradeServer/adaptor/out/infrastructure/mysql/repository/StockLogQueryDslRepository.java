package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface StockLogQueryDslRepository {
    List<StockLogEntity> findStockLogByPageable(Pageable pageable, String uui);
}
