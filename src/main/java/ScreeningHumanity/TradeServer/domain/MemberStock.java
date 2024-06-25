package ScreeningHumanity.TradeServer.domain;

import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.MemberStockOutDto;
import ScreeningHumanity.TradeServer.global.common.exception.CustomException;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 보유 주식 Domain
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberStock {
    private Long id;
    private String uuid; //
    private Long amount; //보유 주식 갯수, 사고 팔때마다 변경.
    private Long totalPrice; //총 매수 금액 --금지
    private Long totalAmount; //총 매수 주식 갯수 --금지
    private String stockCode; //종목 코드
    private String stockName; //종목 이름

    public static MemberStock saleMemberStock(MemberStockOutDto loadMemberStockDto,
            StockUseCase.StockBuySaleDto stockBuyDto) {
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

    public static MemberStock updateMemberStock(MemberStockOutDto loadMemberStockDto,
            StockUseCase.StockBuySaleDto stockBuyDto) {
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

    public static MemberStock createMemberStock(StockUseCase.StockBuySaleDto stockBuyDto, String uuid) {
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

    /**
     * 매수 완료 후, 가지고 있는 보유 주식이 0이 될 경우,
     * totalPrice 와 totalAmount 를 Reset 시킨다.
     * @param memberStock
     * @return
     */
    public static MemberStock resetTotalData(MemberStock memberStock){
        return MemberStock
                .builder()
                .id(memberStock.getId())
                .uuid(memberStock.getUuid())
                .amount(memberStock.getAmount())
                .totalPrice(0L)
                .totalAmount(0L)
                .stockCode(memberStock.getStockCode())
                .stockName(memberStock.getStockName())
                .build();
    }
}
