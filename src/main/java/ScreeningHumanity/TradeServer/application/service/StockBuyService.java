package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.MemberStockOutDto;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveStockLogPort;
import ScreeningHumanity.TradeServer.domain.MemberStock;
import ScreeningHumanity.TradeServer.domain.StockLog;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import ScreeningHumanity.TradeServer.global.common.exception.CustomException;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockBuyService implements StockUseCase {

    private final SaveMemberStockPort saveMemberStockPort;
    private final LoadMemberStockPort loadMemberStockPort;
    private final SaveStockLogPort saveStockLogPort;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void BuyStock(StockBuySaleDto receiveStockBuyDto, String uuid) {
        Optional<MemberStockOutDto> loadMemberStockDto = loadMemberStockPort.LoadMemberStockByUuidAndStockCode(
                uuid, receiveStockBuyDto.getStockCode());

        if (loadMemberStockDto.isEmpty()) {
            MemberStock memberStock = createMemberStock(receiveStockBuyDto, uuid);
            saveMemberStockPort.SaveMemberStock(memberStock);
            saveStockLogPort.saveStockLog(modelMapper.map(receiveStockBuyDto, StockLog.class), StockLogStatus.BUY, uuid);
            return;
        }
        MemberStock memberStock = updateMemberStock(loadMemberStockDto.get(), receiveStockBuyDto);
        saveMemberStockPort.SaveMemberStock(memberStock);
        saveStockLogPort.saveStockLog(modelMapper.map(receiveStockBuyDto, StockLog.class), StockLogStatus.BUY, uuid);
    }

    @Transactional
    @Override
    public void SaleStock(StockBuySaleDto receiveStockSaleDto, String uuid) {
        MemberStockOutDto loadMemberStockDto =
                loadMemberStockPort
                        .LoadMemberStockByUuidAndStockCode(uuid, receiveStockSaleDto.getStockCode())
                        .orElseThrow(() -> new CustomException(
                                BaseResponseCode.SALE_STOCK_NOT_EXIST_ERROR));

        MemberStock memberStock = saleMemberStock(loadMemberStockDto, receiveStockSaleDto);
        saveMemberStockPort.SaveMemberStock(memberStock);
        saveStockLogPort.saveStockLog(modelMapper.map(receiveStockSaleDto, StockLog.class), StockLogStatus.SALE, uuid);
    }

    private MemberStock saleMemberStock(MemberStockOutDto loadMemberStockDto,
            StockBuySaleDto stockBuyDto) {
        Long targetAmount = loadMemberStockDto.getAmount() - stockBuyDto.getAmount();

        if (targetAmount < 0) {
            throw new CustomException(BaseResponseCode.SALE_STOCK_NEGATIVE_TARGET_ERROR);
        }

        return MemberStock
                .builder()
                .id(loadMemberStockDto.getId())
                .uuid(loadMemberStockDto.getUuid())
                .amount(targetAmount)
                .totalPrice(loadMemberStockDto.getTotalPrice())
                .totalAmount(loadMemberStockDto.getTotalAmount())
                .stockCode(loadMemberStockDto.getStockCode())
                .stockName(loadMemberStockDto.getStockName())
                .build();
    }

    private MemberStock updateMemberStock(MemberStockOutDto loadMemberStockDto,
            StockBuySaleDto stockBuyDto) {
        Long targetAmount = loadMemberStockDto.getAmount() + stockBuyDto.getAmount();
        Long targetTotalPrice = loadMemberStockDto.getTotalPrice() + (stockBuyDto.getAmount()
                * stockBuyDto.getPrice());
        Long targetTotalAmount = loadMemberStockDto.getTotalAmount() + stockBuyDto.getAmount();

        return MemberStock
                .builder()
                .id(loadMemberStockDto.getId())
                .uuid(loadMemberStockDto.getUuid())
                .amount(targetAmount)
                .totalPrice(targetTotalPrice)
                .totalAmount(targetTotalAmount)
                .stockCode(loadMemberStockDto.getStockCode())
                .stockName(loadMemberStockDto.getStockName())
                .build();
    }

    private MemberStock createMemberStock(StockBuySaleDto stockBuyDto, String uuid) {
        Long targetTotalPrice = stockBuyDto.getAmount() * stockBuyDto.getPrice();
        return MemberStock
                .builder()
                .uuid(uuid)
                .amount(stockBuyDto.getAmount())
                .totalPrice(targetTotalPrice)
                .totalAmount(stockBuyDto.getAmount())
                .stockCode(stockBuyDto.getStockCode())
                .stockName(stockBuyDto.getStockName())
                .build();
    }
}
