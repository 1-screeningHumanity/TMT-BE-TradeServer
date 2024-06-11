package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.MemberStockOutDto;
import ScreeningHumanity.TradeServer.application.port.out.dto.MessageQueueOutDto;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.MessageQueuePort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveStockLogPort;
import ScreeningHumanity.TradeServer.domain.MemberStock;
import ScreeningHumanity.TradeServer.domain.StockLog;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import ScreeningHumanity.TradeServer.global.common.exception.CustomException;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockBuySaleService implements StockUseCase {

    private final SaveMemberStockPort saveMemberStockPort;
    private final LoadMemberStockPort loadMemberStockPort;
    private final SaveStockLogPort saveStockLogPort;
    private final MessageQueuePort messageQueuePort;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void BuyStock(StockBuySaleDto receiveStockBuyDto, String uuid) {
        Optional<MemberStockOutDto> loadMemberStockDto = loadMemberStockPort.LoadMemberStockByUuidAndStockCode(
                uuid, receiveStockBuyDto.getStockCode());

        if (loadMemberStockDto.isEmpty()) {
            MemberStock memberStock = MemberStock.createMemberStock(receiveStockBuyDto, uuid);
            MemberStock savedData = saveMemberStockPort.SaveMemberStock(memberStock);
            StockLog savedLogData = saveStockLogPort.saveStockLog(
                    modelMapper.map(receiveStockBuyDto, StockLog.class),
                    StockLogStatus.BUY, uuid);

            try {
                messageQueuePort.send("trade-payment-buy",
                        MessageQueueOutDto.BuyDto
                                .builder()
                                .price(memberStock.getTotalPrice())
                                .uuid(uuid)
                                .build()).get();
            } catch (Exception e) {
                log.error("Kafka 연결 확인 필요. 메세지 발행 실패");
                saveMemberStockPort.DeleteMemberStock(savedData);
                saveStockLogPort.deleteStockLog(savedLogData);
            }

            return;
        }
        MemberStock memberStock = MemberStock.updateMemberStock(loadMemberStockDto.get(),
                receiveStockBuyDto);
        MemberStock savedData = saveMemberStockPort.SaveMemberStock(memberStock);
        StockLog savedLog = saveStockLogPort.saveStockLog(
                modelMapper.map(receiveStockBuyDto, StockLog.class),
                StockLogStatus.BUY, uuid);

        try {
            messageQueuePort.send("trade-payment-buy",
                    MessageQueueOutDto.BuyDto
                            .builder()
                            .price(receiveStockBuyDto.getPrice() * receiveStockBuyDto.getAmount())
                            .uuid(uuid)
                            .build()).get();
        } catch (Exception e) {
            log.error("Kafka Messaging 도중, 오류 발생");
            saveMemberStockPort.SaveMemberStock(
                    createBeforeBuyMemberStock(savedData, loadMemberStockDto.get()));
            saveStockLogPort.deleteStockLog(savedLog);
        }
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
        MemberStock savedData = saveMemberStockPort.SaveMemberStock(memberStock);
        StockLog savedLog = saveStockLogPort.saveStockLog(
                modelMapper.map(receiveStockSaleDto, StockLog.class),
                StockLogStatus.SALE, uuid);

        try {
            messageQueuePort.send(
                    "trade-payment-sale",
                    MessageQueueOutDto.BuyDto
                            .builder()
                            .uuid(uuid)
                            .price(receiveStockSaleDto.getPrice()
                                    * receiveStockSaleDto.getAmount())
                            .build()).get();
        } catch (Exception e) {
            log.error("Kafka Messaging 도중, 오류 발생");
            saveMemberStockPort.SaveMemberStock(
                    createBeforeSaleMemberStock(savedData, loadMemberStockDto));
            saveStockLogPort.deleteStockLog(savedLog);
            throw new CustomException(BaseResponseCode.SALE_STOCK_FAIL_ERROR);
        }
    }

    /**
     * 메세지 발행 중, 실패 시, 트랜잭션 롤백 진행을 위한 Domain 생성 매서드
     * @param savedData
     * @param beforeData
     * @return
     */
    private MemberStock createBeforeBuyMemberStock(MemberStock savedData, MemberStockOutDto beforeData){
        return MemberStock.builder()
                .id(savedData.getId())
                .uuid(beforeData.getUuid())
                .amount(beforeData.getAmount())
                .totalPrice(beforeData.getTotalPrice())
                .totalAmount(beforeData.getTotalAmount())
                .stockCode(beforeData.getStockCode())
                .stockName(beforeData.getStockName())
                .build();
    }

    /**
     * 메세지 발행 중, 실패 시, 트랜잭션 롤백 진행을 위한 Domain 생성 매서드
     *
     * @param savedData
     * @param beforeData
     * @return
     */
    private MemberStock createBeforeSaleMemberStock(MemberStock savedData,
            MemberStockOutDto beforeData) {
        return MemberStock.builder()
                .id(savedData.getId())
                .uuid(beforeData.getUuid())
                .amount(beforeData.getAmount())
                .totalPrice(beforeData.getTotalPrice())
                .totalAmount(beforeData.getTotalAmount())
                .stockCode(beforeData.getStockCode())
                .stockName(beforeData.getStockName())
                .build();
    }
}
