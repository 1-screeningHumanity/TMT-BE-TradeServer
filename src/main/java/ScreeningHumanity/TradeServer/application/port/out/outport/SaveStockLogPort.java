package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.StockLog;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;

public interface SaveStockLogPort {
    StockLog saveStockLog(StockLog stockLog, StockLogStatus status, String uuid);

    void deleteStockLog(StockLog stockLog);
}
