package ScreeningHumanity.TradeServer.application.port.in.usecase;

import ScreeningHumanity.TradeServer.application.port.out.dto.StockLogOutDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface StockLogUseCase {

    List<StockLogOutDto> LoadStockLog(Pageable pageable, String uuid);
}
