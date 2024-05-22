package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.MemberStockDto;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveMemberStockPort;
import ScreeningHumanity.TradeServer.domain.MemberStock;
import ScreeningHumanity.TradeServer.global.common.exception.CustomException;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockBuyService implements StockUseCase {

    private final SaveMemberStockPort saveMemberStockPort;
    private final LoadMemberStockPort loadMemberStockPort;

    @Transactional
    @Override
    public void BuyStock(StockBuySaleDto receiveStockBuyDto, String uuid) {
        Optional<MemberStockDto> loadMemberStockDto = loadMemberStockPort.LoadMemberStockByUuidAndStockCode(
                uuid, receiveStockBuyDto.getStockCode());

        if (loadMemberStockDto.isEmpty()) {
            MemberStock memberStock = createMemberStock(receiveStockBuyDto, uuid);
            saveMemberStockPort.SaveMemberStock(memberStock);
            return;
        }
        MemberStock memberStock = updateMemberStock(loadMemberStockDto.get(), receiveStockBuyDto);
        saveMemberStockPort.SaveMemberStock(memberStock);
    }

    @Override
    public void SaleStock(StockBuySaleDto receiveStockSaleDto, String uuid) {
        MemberStockDto loadMemberStockDto =
                loadMemberStockPort
                        .LoadMemberStockByUuidAndStockCode(uuid, receiveStockSaleDto.getStockCode())
                        .orElseThrow(() -> new CustomException(
                                BaseResponseCode.SALE_STOCK_NOT_EXIST_ERROR));

        MemberStock memberStock = saleMemberStock(loadMemberStockDto, receiveStockSaleDto);
        saveMemberStockPort.SaveMemberStock(memberStock);
    }

    private MemberStock saleMemberStock(MemberStockDto loadMemberStockDto,
            StockBuySaleDto stockBuyDto) {
        Long targetAmount = loadMemberStockDto.getAmount() - stockBuyDto.getAmount();

        if (targetAmount < 0) {
            //todo : 여기서 Row 지워줄지, 스케줄링 적용할 지 생각 해봐야됨.
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
                .build();
    }

    private MemberStock updateMemberStock(MemberStockDto loadMemberStockDto,
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
                .build();
    }
}
