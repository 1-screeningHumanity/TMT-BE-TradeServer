package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import ScreeningHumanity.TradeServer.application.port.in.usecase.StockLogUseCase;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadStockLogPort;
import ScreeningHumanity.TradeServer.domain.StockLog;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StockLogService implements StockLogUseCase {

    private final LoadStockLogPort loadStockLogPort;

    @Transactional(readOnly = true)
    @Override
    public List<StockLog> LoadStockLog(Pageable pageable, String uuid) {
        return loadStockLogPort.loadStockLog(pageable, uuid);
    }
}
