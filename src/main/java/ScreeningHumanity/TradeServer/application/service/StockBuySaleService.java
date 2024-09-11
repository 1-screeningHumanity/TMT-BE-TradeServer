package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto.WonInfo;
import ScreeningHumanity.TradeServer.application.port.in.dto.StockInDto;
import ScreeningHumanity.TradeServer.application.port.in.usecase.PaymentUseCase;
import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
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
import java.time.LocalDateTime;
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
    private final PaymentUseCase paymentUseCase;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void saleStock(StockInDto.Sale saleDto, String uuid) {

        //기존 데이터 찾기
        MemberStock findData = loadMemberStockPort.loadMemberStock(uuid, saleDto.getStockCode())
                .orElseThrow(() -> new CustomException(BaseResponseCode.SALE_STOCK_NOT_EXIST_ERROR));

        //매도 진행
        MemberStock newData = saleMemberStock(saleDto, findData);
        saveMemberStockPort.saveMemberStock(newData);

        //판매 로그 등록
        saveStockLogPort.saveStockLog(
                modelMapper.map(saleDto, StockLog.class), StockLogStatus.SALE, uuid);

        //Payment 서버에 Cash 차감 요청
        if (Boolean.FALSE.equals(sendSaleMessageUpdateCash(saleDto, uuid))) {
            throw new CustomException(BaseResponseCode.SALE_STOCK_FAIL_ERROR);
        }

        //Notification 서버에 알림 전달
        sendSaleNotification(saleDto, uuid);
    }

    @Transactional
    @Override
    public void buyStock(StockInDto.Buy buyDto, String uuid, String accessToken) {

        //현재 회원의 Cash 검증
        if (Boolean.FALSE.equals(isMemberCashSufficient(buyDto, accessToken))) {
            throw new CustomException(BaseResponseCode.BUY_STOCK_NOT_ENOUGH_WON);
        }

        //회원의 주식 정보 불러오기
        //없을경우, 초기화 객체 생성
        MemberStock loadData = loadMemberStockPort.loadMemberStock(uuid,
                buyDto.getStockCode()).orElseGet(() -> createEmptyMemberStock(buyDto, uuid));

        //매수 진행
        MemberStock newData = buyMemberStock(buyDto, loadData);
        saveMemberStockPort.saveMemberStock(newData);

        //로그 등록
        saveStockLogPort.saveStockLog(
                modelMapper.map(buyDto, StockLog.class), StockLogStatus.BUY, uuid);

        //Payment 서버에 Cash 차감 요청
        if (Boolean.FALSE.equals(sendBuyMessageUpdateCash(buyDto, uuid))) {
            throw new CustomException(BaseResponseCode.BUY_STOCK_FAIL_ERROR);
        }

        //Notification 서버에 알림 전달
        sendBuyNotification(buyDto, uuid);
    }

    private Boolean sendBuyMessageUpdateCash(StockInDto.Buy dto, String uuid) {
        try {
            messageQueuePort.send("trade-payment-buy",
                    MessageQueueOutDto.BuyDto
                            .builder()
                            .price(dto.getPrice() * dto.getAmount())
                            .uuid(uuid)
                            .build()).get();
            return true;
        } catch (Exception e) {
            log.error("[일반 매수] 진행 중, 오류 발생-1");
            return false;
        }
    }

    private Boolean sendSaleMessageUpdateCash(StockInDto.Sale dto, String uuid) {
        try {
            messageQueuePort.send(
                    "trade-payment-sale",
                    MessageQueueOutDto.BuyDto
                            .builder()
                            .uuid(uuid)
                            .price(dto.getPrice() * dto.getAmount())
                            .build()).get();
            return true;
        } catch (Exception e) {
            log.error("[일반 매도] 진행 중, 오류 발생-1");
            return false;
        }
    }

    private void sendSaleNotification(StockInDto.Sale dto, String uuid) {
        String bodyData =
                "종목명 : " + dto.getStockName() + "\n"
                        + "수량 : " + dto.getAmount() + "\n"
                        + "총 가격 : "
                        + dto.getAmount() * dto.getPrice()
                        + "\n"
                        + " 매도 체결 완료 되었습니다.";
        messageQueuePort.sendNotification(MessageQueueOutDto.TradeStockNotificationDto
                .builder()
                .title("매도 체결 완료")
                .body(bodyData)
                .uuid(uuid)
                .notificationLogTime(LocalDateTime.now().toString())
                .build());
    }

    private void sendBuyNotification(StockInDto.Buy dto, String uuid) {
        //매수 완료 알람 Message 전달
        String bodyData =
                "종목명 : " + dto.getStockName() + "\n"
                        + "수량 : " + dto.getAmount() + "\n"
                        + "총 가격 : " + dto.getAmount() * dto.getPrice()
                        + "\n"
                        + " 매수 체결 완료 되었습니다.";

        messageQueuePort.sendNotification(MessageQueueOutDto.TradeStockNotificationDto
                .builder()
                .title("매수 체결 완료")
                .body(bodyData)
                .uuid(uuid)
                .notificationLogTime(LocalDateTime.now().toString())
                .build());
    }

    private MemberStock buyMemberStock(StockInDto.Buy dto, MemberStock memberStock) {

        Long totalPrice = dto.getPrice() * dto.getAmount();
        Long newAmount = memberStock.getAmount() + dto.getAmount();
        Long newTotalPrice = memberStock.getTotalPrice() + totalPrice;
        Long newTotalAmount = memberStock.getTotalAmount() + dto.getAmount();

        return MemberStock
                .builder()
                .id(memberStock.getId())
                .uuid(memberStock.getUuid())
                .amount(newAmount)
                .totalPrice(newTotalPrice)
                .totalAmount(newTotalAmount)
                .stockCode(memberStock.getStockCode())
                .stockName(memberStock.getStockName())
                .build();
    }

    private MemberStock saleMemberStock(StockInDto.Sale saleDto, MemberStock findData) {
        long targetAmount = findData.getAmount() - saleDto.getAmount();

        if(targetAmount < 0){
            throw new CustomException(BaseResponseCode.SALE_STOCK_NEGATIVE_TARGET_ERROR);
        }

        return MemberStock
                .builder()
                .id(findData.getId())
                .uuid(findData.getUuid())
                .amount(targetAmount)
                .totalPrice(targetAmount == 0 ? 0 : findData.getTotalPrice())
                .totalAmount(targetAmount == 0 ? 0 : findData.getTotalAmount())
                .stockCode(findData.getStockCode())
                .stockName(findData.getStockName())
                .build();
    }

    private MemberStock createEmptyMemberStock(StockInDto.Buy dto, String uuid) {
        return MemberStock
                .builder()
                .id(null)
                .uuid(uuid)
                .amount(0L)
                .totalPrice(0L)
                .totalAmount(0L)
                .stockCode(dto.getStockCode())
                .stockName(dto.getStockName())
                .build();
    }

    private boolean isMemberCashSufficient(StockInDto.Buy buyDto, String accessToken) {
        WonInfo findData = paymentUseCase.searchMemberCash(accessToken);
        Long totalPrice = buyDto.getAmount() * buyDto.getPrice();
        if (findData.getWon() < totalPrice) {
            return false;
        }
        return true;
    }
}
