package ScreeningHumanity.TradeServer.application.port.in.usecase;

import ScreeningHumanity.TradeServer.domain.StockLog;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface StockLogUseCase {
    List<StockLog> LoadStockLog(Pageable pageable, String uuid);
}
