package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.StockLogJpaRepository;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadStockLogPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveStockLogPort;
import ScreeningHumanity.TradeServer.domain.StockLog;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockLogAdaptor implements SaveStockLogPort, LoadStockLogPort {

    private final StockLogJpaRepository stockLogJpaRepository;
    private final ModelMapper modelMapper;

    @Override
    public StockLog saveStockLog(StockLog stockLog, StockLogStatus status, String uuid) {
        StockLogEntity saveData = stockLogJpaRepository.save(
                StockLogEntity.toEntityFrom(stockLog, status, uuid));
        return StockLogEntity.toDomainFrom(saveData);
    }

    @Override
    public void deleteStockLog(StockLog stockLog) {
        stockLogJpaRepository.deleteById(stockLog.getId());
    }

    @Override
    public List<StockLog> loadStockLog(Pageable pageable, String uuid) {
        List<StockLogEntity> stockLogByPageable = stockLogJpaRepository.findStockLogByPageable(pageable, uuid);
         return stockLogByPageable.stream()
                 .map(entity -> modelMapper.map(entity, StockLog.class))
                 .collect(Collectors.toList());
    }
}
