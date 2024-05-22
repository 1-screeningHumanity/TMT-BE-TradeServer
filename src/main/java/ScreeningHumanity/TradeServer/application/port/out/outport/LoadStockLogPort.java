package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.StockLog;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface LoadStockLogPort {
    List<StockLog> loadStockLog(Pageable pageable, String uuid);
}
