package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto.WonInfo;
import ScreeningHumanity.TradeServer.application.port.in.dto.ReservationStockInDto;
import ScreeningHumanity.TradeServer.application.port.in.usecase.PaymentUseCase;
import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.MessageQueueOutDto;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationStockOutDto;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadReservationStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.MessageQueuePort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveReservationStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveStockLogPort;
import ScreeningHumanity.TradeServer.domain.MemberStock;
import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import ScreeningHumanity.TradeServer.domain.StockLog;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import ScreeningHumanity.TradeServer.global.common.exception.CustomException;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationStockService implements ReservationStockUseCase {

    private final LoadReservationStockPort loadReservationStockPort;
    private final SaveReservationStockPort saveReservationStockPort;
    private final SaveMemberStockPort saveMemberStockPort;
    private final LoadMemberStockPort loadMemberStockPort;
    private final SaveStockLogPort saveStockLogPort;
    private final MessageQueuePort messageQueuePort;
    private final ModelMapper modelMapper;
    private final PaymentUseCase paymentUseCase;

    public static final String STATUS_BUY = "매수";
    public static final String STATUS_SALE = "매도";

    @Transactional
    @Override
    public void buyStock(ReservationStockInDto.Buy dto, String uuid, String accessToken) {

        //검증
        if (Boolean.FALSE.equals(isMemberCashSufficient(dto, accessToken))) {
            throw new CustomException(BaseResponseCode.BUY_STOCK_NOT_ENOUGH_WON);
        }

        //예약 매수 등록
        ReservationBuy reservationBuy = createReservationBuyStock(dto, uuid);
        saveReservationStockPort.saveReservationBuyStock(reservationBuy);

        //Payment 서버에 Cash 차감 요청
        if (Boolean.FALSE.equals(sendReservationBuyMessageUpdateCash(dto, uuid))) {
            throw new CustomException(BaseResponseCode.BUY_RESERVATION_STOCK_FAIL_ERROR);
        }
    }

    @Transactional
    @Override
    public void saleStock(ReservationStockInDto.Sale dto, String uuid) {
        //기존 데이터 찾기
        MemberStock findData = loadMemberStockPort.loadMemberStock(uuid, dto.getStockCode())
                .orElseThrow(() -> new CustomException(
                        BaseResponseCode.SALE_RESERVATION_STOCK_NOTFOUND_ERROR));

        //예약 매도 등록 가능 검증
        if (Boolean.TRUE.equals(isAllStockAlreadyReservationForSell(
                dto.getStockCode(), uuid, dto.getAmount()))) {
            throw new CustomException(BaseResponseCode.SALE_RESERVATION_ALL_STOCK_REGISTERED);
        }

        //예약 매도 등록
        ReservationSale newData = createReservationSaleStock(dto, findData);
        saveReservationStockPort.saveReservationSaleStock(newData);
    }

    @Transactional
    @Override
    public void doReservationStock(ReservationStockInDto.RealTimeStockInfo dto) {
        doReservationBuyStock(dto);
        doReservationSaleStock(dto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationStockOutDto.Logs> buySaleLog(String uuid) {
        List<ReservationBuy> reservationBuys = loadReservationStockPort.loadReservationBuy(uuid);
        List<ReservationSale> reservationSales = loadReservationStockPort.loadReservationSale(uuid);

        List<ReservationStockOutDto.Logs> result = reservationBuys.stream()
                .map(this::convertToBuyDto)
                .collect(Collectors.toList());

        result.addAll(reservationSales.stream()
                .map(this::convertToSaleDto)
                .toList());

        result.sort(Comparator.comparing(ReservationStockOutDto.Logs::getCreatedAt).reversed());
        result.forEach(
                m -> m.setTime(m.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));

        return result;
    }

    @Transactional
    @Override
    public void cancelReservationSaleStock(Long saleId, boolean messageFlag) {
        //id로 삭제 진행
        ReservationSale findData = saveReservationStockPort.deleteReservationSaleStock(
                saleId).orElseThrow(
                () -> new CustomException(BaseResponseCode.DELETE_RESERVATION_SALE_STOCK_ERROR));

        //취소 알림 전송
        if (Boolean.TRUE.equals(messageFlag)) {
            sendCancelReservationSaleNotification(findData);
        }
    }

    @Transactional
    @Override
    public void cancelReservationBuyStock(Long saleId, boolean messageFlag) {
        //id로 삭제 진행
        ReservationBuy findData = saveReservationStockPort.deleteReservationBuyStock(saleId)
                .orElseThrow(() ->
                        new CustomException(BaseResponseCode.DELETE_RESERVATION_BUY_STOCK_ERROR));

        //보유 Cash Update Message 전송
        if (Boolean.FALSE.equals(sendReservationBuyCancelMessageUpdateCash(findData))) {
            throw new CustomException(BaseResponseCode.BUY_RESERVATION_STOCK_CANCEL_FAIL_ERROR);
        }

        //취소 알림 전송
        if (Boolean.TRUE.equals(messageFlag)) {
            sendCancelReservationBuyNotification(findData);
        }
    }

    private void doReservationSaleStock(ReservationStockInDto.RealTimeStockInfo dto) {
        List<ReservationSale> matchSaleStockList = loadReservationStockPort.findMatchSaleStock(
                dto.getStockCode(), dto.getPrice());
        if (!matchSaleStockList.isEmpty()) {
            for (ReservationSale matchSaleStock : matchSaleStockList) {

                MemberStock newData = saleMemberStock(matchSaleStock);
                saveMemberStockPort.saveMemberStock(newData);
                cancelReservationSaleStock(matchSaleStock.getId(), false);

                //로그 등록
                saveStockLogPort.saveStockLog(
                        modelMapper.map(matchSaleStock, StockLog.class),
                        StockLogStatus.RESERVATION_SALE,
                        matchSaleStock.getUuid());

                //Payment 서버에 Cash 증감 요청
                if (Boolean.FALSE.equals(sendSaleMessageUpdateCash(
                        matchSaleStock.getUuid(),
                        matchSaleStock.getAmount() * matchSaleStock.getPrice()))) {
                    throw new CustomException(BaseResponseCode.SALE_STOCK_FAIL_ERROR);
                }

                //Notification 서버에 알림 전달
                sendSaleNotification(
                        matchSaleStock.getStockName(),
                        matchSaleStock.getAmount(),
                        matchSaleStock.getPrice(),
                        matchSaleStock.getUuid());
            }
        }
    }

    private void doReservationBuyStock(ReservationStockInDto.RealTimeStockInfo dto) {
        List<ReservationBuy> matchBuyStockList = loadReservationStockPort.findMatchBuyStock(
                dto.getStockCode(), dto.getPrice());
        if (!matchBuyStockList.isEmpty()) {
            for (ReservationBuy matchSaleStock : matchBuyStockList) {

                MemberStock newData = buyMemberStock(matchSaleStock);
                saveMemberStockPort.saveMemberStock(newData);
                cancelReservationBuyStock(matchSaleStock.getId(), false);

                //로그 등록
                saveStockLogPort.saveStockLog(
                        modelMapper.map(matchSaleStock, StockLog.class),
                        StockLogStatus.RESERVATION_BUY,
                        matchSaleStock.getUuid());

                //Notification 서버에 알림 전달
                sendBuyNotification(
                        matchSaleStock.getStockName(),
                        matchSaleStock.getAmount(),
                        matchSaleStock.getPrice(),
                        matchSaleStock.getUuid()
                );
            }
        }
    }

    private void sendCancelReservationBuyNotification(ReservationBuy findData) {
        String bodyData =
                "종목명 : " + findData.getStockName() + "\n"
                        + "수량 : " + findData.getAmount() + "개\n"
                        + "총 가격 : " + findData.getAmount() * findData.getPrice() + "원\n"
                        + "예약 매수 취소 되었습니다.";
        messageQueuePort.sendNotification(MessageQueueOutDto.TradeStockNotificationDto
                .builder()
                .title("예약 매수 취소 완료")
                .body(bodyData)
                .uuid(findData.getUuid())
                .notificationLogTime(LocalDateTime.now().toString())
                .build());
    }

    private void sendCancelReservationSaleNotification(ReservationSale findData) {
        String bodyData =
                "종목명 : " + findData.getStockName() + "\n"
                        + "수량 : " + findData.getAmount() + "개\n"
                        + "총 가격 : " + findData.getAmount() * findData.getPrice() + "원\n"
                        + "예약 매도 취소 되었습니다.";
        messageQueuePort.sendNotification(MessageQueueOutDto.TradeStockNotificationDto
                .builder()
                .title("예약 매도 취소 완료")
                .body(bodyData)
                .uuid(findData.getUuid())
                .notificationLogTime(LocalDateTime.now().toString())
                .build());
    }

    private ReservationSale createReservationSaleStock(ReservationStockInDto.Sale dto,
            MemberStock findData) {
        long targetAmount = findData.getAmount() - dto.getAmount();

        if (targetAmount < 0) {
            throw new CustomException(BaseResponseCode.SALE_RESERVATION_STOCK_AMOUNT_ERROR);
        }

        return ReservationSale
                .builder()
                .uuid(findData.getUuid())
                .price(dto.getPrice())
                .amount(dto.getAmount())
                .stockCode(findData.getStockCode())
                .stockName(findData.getStockName())
                .build();
    }

    private Boolean sendReservationBuyCancelMessageUpdateCash(ReservationBuy findData) {
        try {
            messageQueuePort.send(
                    "trade-payment-reservationcancel",
                    MessageQueueOutDto.ReservationBuyCancelDto
                            .builder()
                            .uuid(findData.getUuid())
                            .price(findData.getPrice() * findData.getAmount())
                            .build()).get();
            return true;
        } catch (Exception e) {
            log.error("[예약 매수 취소] 진행 중, 오류 발생-1");
            return false;
        }
    }

    private Boolean sendReservationBuyMessageUpdateCash(ReservationStockInDto.Buy dto,
            String uuid) {
        try {
            messageQueuePort.send("trade-payment-buy",
                    MessageQueueOutDto.BuyDto
                            .builder()
                            .price(dto.getPrice() * dto.getAmount())
                            .uuid(uuid)
                            .build()).get();
            return true;
        } catch (Exception e) {
            log.error("[예약 매수] 진행 중, 오류 발생-1");
            return false;
        }
    }

    private void sendSaleNotification(String stockName, Long amount, Long price, String uuid) {
        //알림 메세지 전송
        String bodyData =
                "종목명 : " + stockName + "\n"
                        + "수량 : " + amount + "개\n"
                        + "총 가격 : "
                        + amount * price + "원\n"
                        + "예약 매도 체결 완료 되었습니다.";
        messageQueuePort.sendNotification(MessageQueueOutDto.TradeStockNotificationDto
                .builder()
                .title("예약 매도 체결 완료")
                .body(bodyData)
                .uuid(uuid)
                .notificationLogTime(LocalDateTime.now().toString())
                .build());
    }

    private void sendBuyNotification(String stockName, Long amount, Long price, String uuid) {
        String bodyData =
                "종목명 : " + stockName + "\n"
                        + "수량 : " + amount + "개\n"
                        + "총 가격 : " + amount * price
                        + "원\n"
                        + "예약 매수 체결 완료 되었습니다.";
        messageQueuePort.sendNotification(MessageQueueOutDto.TradeStockNotificationDto
                .builder()
                .title("예약 매수 체결 완료")
                .body(bodyData)
                .uuid(uuid)
                .notificationLogTime(LocalDateTime.now().toString())
                .build());
    }

    private boolean sendSaleMessageUpdateCash(String uuid, long price) {
        try {
            messageQueuePort.send(
                    "trade-payment-sale",
                    MessageQueueOutDto.BuyDto
                            .builder()
                            .uuid(uuid)
                            .price(price)
                            .build()).get();

            return true;
        } catch (Exception e) {
            log.error("[일반 매도] 진행 중, 오류 발생-1");
            return false;
        }
    }

    private MemberStock buyMemberStock(ReservationBuy matchBuyStock) {
        MemberStock targetData = loadMemberStockPort.loadMemberStock(matchBuyStock.getUuid(),
                matchBuyStock.getStockCode()).orElse(MemberStock
                .builder()
                .uuid(matchBuyStock.getUuid())
                .amount(0L)
                .totalPrice(0L)
                .totalAmount(0L)
                .stockCode(matchBuyStock.getStockCode())
                .stockName(matchBuyStock.getStockName())
                .build());

        return MemberStock
                .builder()
                .id(targetData.getId())
                .uuid(targetData.getUuid())
                .amount(targetData.getAmount() + matchBuyStock.getAmount())
                .totalPrice(targetData.getTotalPrice() + (matchBuyStock.getAmount()
                        * matchBuyStock.getPrice()))
                .totalAmount(targetData.getTotalAmount() + matchBuyStock.getAmount())
                .stockCode(targetData.getStockCode())
                .stockName(targetData.getStockName())
                .build();
    }

    private MemberStock saleMemberStock(ReservationSale matchSaleStock) {

        //검증
        //가지고 있는 주식이 있는지?
        MemberStock targetData = loadMemberStockPort.loadMemberStock(
                matchSaleStock.getUuid(), matchSaleStock.getStockCode()).orElseThrow(
                () -> new CustomException(BaseResponseCode.SALE_RESERVATION_STOCK_NOTFOUND_ERROR));

        long targetAmount = targetData.getAmount() - matchSaleStock.getAmount();

        if (targetAmount < 0) {
            throw new CustomException(BaseResponseCode.SALE_RESERVATION_STOCK_AMOUNT_ERROR);
        }

        //매도 후 Domain 생성
        return MemberStock
                .builder()
                .id(targetData.getId())
                .uuid(targetData.getUuid())
                .amount(targetAmount)
                .totalPrice(targetAmount == 0 ? 0 : targetData.getTotalPrice())
                .totalAmount(targetAmount == 0 ? 0 : targetData.getTotalAmount())
                .stockCode(targetData.getStockCode())
                .stockName(targetData.getStockName())
                .build();
    }

    private ReservationStockOutDto.Logs convertToBuyDto(ReservationBuy buy) {
        ReservationStockOutDto.Logs dto = modelMapper.map(buy, ReservationStockOutDto.Logs.class);

        dto.setTotalPrice(String.valueOf(buy.getPrice() * buy.getAmount()));
        dto.setStatus(STATUS_BUY);

        return dto;
    }

    private ReservationStockOutDto.Logs convertToSaleDto(ReservationSale sale) {
        ReservationStockOutDto.Logs dto = modelMapper.map(sale, ReservationStockOutDto.Logs.class);

        dto.setTotalPrice(String.valueOf(sale.getPrice() * sale.getAmount()));
        dto.setStatus(STATUS_SALE);

        return dto;
    }

    private ReservationBuy createReservationBuyStock(ReservationStockInDto.Buy dto, String uuid) {
        return ReservationBuy
                .builder()
                .uuid(uuid)
                .price(dto.getPrice())
                .amount(dto.getAmount())
                .stockCode(dto.getStockCode())
                .stockName(dto.getStockName())
                .build();
    }

    private boolean isMemberCashSufficient(ReservationStockInDto.Buy buyDto, String accessToken) {
        WonInfo findData = paymentUseCase.searchMemberCash(accessToken);
        Long totalPrice = buyDto.getAmount() * buyDto.getPrice();
        if (findData.getWon() < totalPrice) {
            return false;
        }
        return true;
    }

    private Boolean isAllStockAlreadyReservationForSell(
            String stockCode, String uuid, Long targetAmount) {
        Long totalReservedAmount = loadReservationStockPort.countSaleStockByStockCode(stockCode,
                uuid).orElse(0L);
        return totalReservedAmount >= targetAmount;
    }
}
