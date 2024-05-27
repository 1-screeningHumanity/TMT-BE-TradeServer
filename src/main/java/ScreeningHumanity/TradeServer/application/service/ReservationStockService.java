package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationLogOutDto;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadReservationStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveReservationStockPort;
import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationStockService implements ReservationStockUseCase {

    private final LoadReservationStockPort loadReservationStockPort;
    private final SaveReservationStockPort saveReservationStockPort;
    private final ModelMapper modelMapper;

    public static final String STATUS_BUY = "매수";
    public static final String STATUS_SALE = "매도";


    @Override
    public void BuyStock(StockBuySaleDto receiveStockBuyDto, String uuid) {

        ReservationBuy reservationBuy = createReservationBuyStock(receiveStockBuyDto, uuid);

        saveReservationStockPort.SaveReservationBuyStock(reservationBuy);
    }

    @Override
    public void SaleStock(StockBuySaleDto stockBuyDto, String uuid) {
        ReservationSale reservationSaleStock = createReservationSaleStock(stockBuyDto, uuid);

        saveReservationStockPort.SaveReservationSaleStock(reservationSaleStock);
    }

    @Override
    public List<ReservationLogOutDto> BuySaleLog(String uuid) {
        List<ReservationBuy> reservationBuys = loadReservationStockPort.loadReservationBuy(uuid);
        List<ReservationSale> reservationSales = loadReservationStockPort.loadReservationSale(uuid);

        List<ReservationLogOutDto> result = reservationBuys.stream()
                .map(this::convertToBuyDto)
                .collect(Collectors.toList());

        result.addAll(reservationSales.stream()
                .map(this::convertToSaleDto)
                .collect(Collectors.toList()));

        result.sort(Comparator.comparing(ReservationLogOutDto::getCreatedAt).reversed());
        result.forEach(
                m -> m.setTime(m.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));

        return result;
    }

    private ReservationLogOutDto convertToBuyDto(ReservationBuy buy) {
        ReservationLogOutDto dto = modelMapper.map(buy, ReservationLogOutDto.class);

//        dto.setTime(buy.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        dto.setTotalPrice(String.valueOf(buy.getPrice() * buy.getAmount()));
        dto.setStatus(STATUS_BUY);

        return dto;
    }

    private ReservationLogOutDto convertToSaleDto(ReservationSale sale) {
        ReservationLogOutDto dto = modelMapper.map(sale, ReservationLogOutDto.class);

//        dto.setTime(sale.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        dto.setTotalPrice(String.valueOf(sale.getPrice() * sale.getAmount()));
        dto.setStatus(STATUS_SALE);

        return dto;
    }

    private ReservationBuy createReservationBuyStock(StockBuySaleDto receiveStockBuyDto,
            String uuid) {
        return ReservationBuy
                .builder()
                .uuid(uuid)
                .price(receiveStockBuyDto.getPrice())
                .amount(receiveStockBuyDto.getAmount())
                .stockCode(receiveStockBuyDto.getStockCode())
                .stockName(receiveStockBuyDto.getStockName())
                .build();
    }

    private ReservationSale createReservationSaleStock(StockBuySaleDto receiveStockBuyDto,
            String uuid) {
        return ReservationSale
                .builder()
                .uuid(uuid)
                .price(receiveStockBuyDto.getPrice())
                .amount(receiveStockBuyDto.getAmount())
                .stockCode(receiveStockBuyDto.getStockCode())
                .stockName(receiveStockBuyDto.getStockName())
                .build();
    }
}
