package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.application.port.in.usecase.StockLogUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.StockLogOutDto;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadStockLogPort;
import ScreeningHumanity.TradeServer.domain.StockLog;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StockLogService implements StockLogUseCase {

    private final LoadStockLogPort loadStockLogPort;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public List<StockLogOutDto> LoadStockLog(Pageable pageable, String uuid) {
        List<StockLog> stockLogs = loadStockLogPort.loadStockLog(pageable, uuid);

        AtomicLong indexId = new AtomicLong(1L);
        return stockLogs.stream()
                .map(stockLog -> convertToDto(stockLog, indexId.getAndIncrement()))
                .collect(Collectors.toList());

    }

    private StockLogOutDto convertToDto(StockLog stockLog, Long indexId) {
        StockLogOutDto dto = modelMapper.map(stockLog, StockLogOutDto.class);

        dto.setTime(stockLog.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        dto.setTotalPrice(String.valueOf(stockLog.getPrice() * stockLog.getAmount()));
        dto.setIndexId(indexId);

        return dto;
    }
}
