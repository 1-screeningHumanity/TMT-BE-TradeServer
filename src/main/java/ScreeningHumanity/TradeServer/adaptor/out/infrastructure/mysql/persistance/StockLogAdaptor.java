package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.StockLogJpaRepository;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadStockLogPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveStockLogPort;
import ScreeningHumanity.TradeServer.domain.StockLog;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockLogAdaptor implements SaveStockLogPort, LoadStockLogPort {

    private final StockLogJpaRepository stockLogJpaRepository;

    @Override
    public void saveStockLog(StockLog stockLog, StockLogStatus status, String uuid) {
        stockLogJpaRepository.save(StockLogEntity.toEntityFrom(stockLog, status, uuid));
    }

}
