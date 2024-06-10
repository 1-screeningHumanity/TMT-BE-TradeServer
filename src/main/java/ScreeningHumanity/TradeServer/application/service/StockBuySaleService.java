package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.MemberStockOutDto;
import ScreeningHumanity.TradeServer.application.port.out.dto.NotificationOutDto;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.NotificationPort;
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
public class StockBuySaleService implements StockUseCase {

    private final SaveMemberStockPort saveMemberStockPort;
    private final LoadMemberStockPort loadMemberStockPort;
    private final SaveStockLogPort saveStockLogPort;
    private final NotificationPort notificationPort;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void BuyStock(StockBuySaleDto receiveStockBuyDto, String uuid) {
        Optional<MemberStockOutDto> loadMemberStockDto = loadMemberStockPort.LoadMemberStockByUuidAndStockCode(
                uuid, receiveStockBuyDto.getStockCode());

        if (loadMemberStockDto.isEmpty()) {
            MemberStock memberStock = MemberStock.createMemberStock(receiveStockBuyDto, uuid);
            saveMemberStockPort.SaveMemberStock(memberStock);
            saveStockLogPort.saveStockLog(modelMapper.map(receiveStockBuyDto, StockLog.class),
                    StockLogStatus.BUY, uuid);

            notificationPort.send("trade-payment-buy",
                    NotificationOutDto.BuyDto
                            .builder()
                            .price(memberStock.getTotalPrice())
                            .uuid(uuid)
                            .build());

            return;
        }
        MemberStock memberStock = MemberStock.updateMemberStock(loadMemberStockDto.get(),
                receiveStockBuyDto);
        saveMemberStockPort.SaveMemberStock(memberStock);
        saveStockLogPort.saveStockLog(modelMapper.map(receiveStockBuyDto, StockLog.class),
                StockLogStatus.BUY, uuid);

        notificationPort.send("trade-payment-buy",
                NotificationOutDto.BuyDto
                        .builder()
                        .price(receiveStockBuyDto.getPrice() * receiveStockBuyDto.getAmount())
                        .uuid(uuid)
                        .build());
    }

    @Transactional
    @Override
    public void SaleStock(StockBuySaleDto receiveStockSaleDto, String uuid) {
        MemberStockOutDto loadMemberStockDto =
                loadMemberStockPort
                        .LoadMemberStockByUuidAndStockCode(uuid, receiveStockSaleDto.getStockCode())
                        .orElseThrow(() -> new CustomException(
                                BaseResponseCode.SALE_STOCK_NOT_EXIST_ERROR));

        MemberStock memberStock = MemberStock.saleMemberStock(loadMemberStockDto,
                receiveStockSaleDto);
        saveMemberStockPort.SaveMemberStock(memberStock);
        saveStockLogPort.saveStockLog(modelMapper.map(receiveStockSaleDto, StockLog.class),
                StockLogStatus.SALE, uuid);
    }
}
