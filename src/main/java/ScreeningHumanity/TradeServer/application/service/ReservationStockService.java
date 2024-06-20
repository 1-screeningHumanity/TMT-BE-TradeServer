package ScreeningHumanity.TradeServer.application.service;

import ScreeningHumanity.TradeServer.adaptor.in.feignclient.PaymentFeignClient;
import ScreeningHumanity.TradeServer.adaptor.in.feignclient.vo.RequestVo;
import ScreeningHumanity.TradeServer.adaptor.in.kafka.dto.RealChartInputDto;
import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.MemberStockOutDto;
import ScreeningHumanity.TradeServer.application.port.out.dto.MessageQueueOutDto;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationLogOutDto;
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
import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private final PaymentFeignClient paymentFeignClient;

    public static final String STATUS_BUY = "매수";
    public static final String STATUS_SALE = "매도";

    @Transactional
    @Override
    public void BuyStock(StockBuySaleDto receiveStockBuyDto, String uuid, String accessToken) {

        BaseResponse<RequestVo.WonInfo> findData = paymentFeignClient.searchMemberCash(accessToken);

        if(findData.result().getWon() < receiveStockBuyDto.getAmount() * receiveStockBuyDto.getPrice()){
            throw new CustomException(BaseResponseCode.BUY_STOCK_NOT_ENOUGH_WON);
        }

        ReservationBuy reservationBuy = createReservationBuyStock(receiveStockBuyDto, uuid);

        ReservationBuy savedData = saveReservationStockPort.SaveReservationBuyStock(
                reservationBuy);

        try {
            messageQueuePort.send("trade-payment-buy",
                    MessageQueueOutDto.BuyDto
                            .builder()
                            .price(reservationBuy.getPrice() * reservationBuy.getAmount())
                            .uuid(uuid)
                            .build()).get();
        } catch (Exception e) {
            log.error("Kafka 연결 확인 필요. 메세지 발행 실패");
            saveReservationStockPort.DeleteReservationBuyStock(savedData.getId());
            throw new CustomException(BaseResponseCode.BUY_RESERVATION_STOCK_FAIL_ERROR);
        }
    }

    /**
     * 예약 매도 Fail의 경우 1. 예약 매도할 주식 정보가 없는 경우. 2. 예약 매도할 수량보다 가지고 있는 수량이 적은 경우.
     *
     * @param stockBuyDto
     * @param uuid
     */
    @Transactional
    @Override
    public void SaleStock(StockBuySaleDto stockBuyDto, String uuid) {
        MemberStockOutDto loadStockData = loadMemberStockPort.LoadMemberStockByUuidAndStockCode(
                uuid, stockBuyDto.getStockCode()).orElseThrow(
                () -> new CustomException(BaseResponseCode.SALE_RESERVATION_STOCK_NOTFOUND_ERROR));

        if (loadStockData.getAmount() < stockBuyDto.getAmount()) {
            throw new CustomException(BaseResponseCode.SALE_RESERVATION_STOCK_AMOUNT_ERROR);
        }

        ReservationSale reservationSaleStock = createReservationSaleStock(stockBuyDto, uuid);

        saveReservationStockPort.SaveReservationSaleStock(reservationSaleStock);
    }

    @Transactional(readOnly = true)
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

    @Transactional
    @Override
    public void DeleteSaleStock(Long saleId) {
        ReservationSale findData = saveReservationStockPort.DeleteReservationSaleStock(
                saleId);

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

    @Transactional
    @Override
    public void DeleteBuyStock(Long saleId) {
        ReservationBuy findData = saveReservationStockPort.DeleteReservationBuyStock(
                saleId);

        try {
            messageQueuePort.send(
                    "trade-payment-reservationcancel",
                    MessageQueueOutDto.ReservationBuyCancelDto
                            .builder()
                            .uuid(findData.getUuid())
                            .price(findData.getPrice() * findData.getAmount())
                            .build()).get();
        } catch (Exception e) {
            log.error("Kafka Messaging 도중, 오류 발생");
            saveReservationStockPort.SaveReservationBuyStock(findData);
            throw new CustomException(BaseResponseCode.BUY_RESERVATION_STOCK_CANCEL_FAIL_ERROR);
        }

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

    @Transactional
    @Override
    public void concludeStock(RealChartInputDto dto) {
        List<ReservationBuy> matchBuyStock = loadReservationStockPort.findMatchBuyStock(dto);
        List<ReservationSale> matchSaleStock = loadReservationStockPort.findMatchSaleStock(dto);

        if (!matchBuyStock.isEmpty()) {
            log.info("예약 매수 start = {}", matchBuyStock.get(0).getStockName());

            saveReservationStockPort.concludeBuyStock(matchBuyStock);

            for (ReservationBuy reservationBuy : matchBuyStock) {
                Optional<MemberStockOutDto> memberStockOutDto = loadMemberStockPort.LoadMemberStockByUuidAndStockCode(
                        reservationBuy.getUuid(), reservationBuy.getStockCode());

                StockUseCase.StockBuySaleDto data = modelMapper.map(reservationBuy,
                        StockUseCase.StockBuySaleDto.class);
                if (memberStockOutDto.isEmpty()) {
                    MemberStock memberStock = MemberStock.createMemberStock(data, data.getUuid());
                    saveMemberStockPort.SaveMemberStock(memberStock);
                    saveStockLogPort.saveStockLog(modelMapper.map(data, StockLog.class),
                            StockLogStatus.RESERVATION_BUY, data.getUuid());
                    return;
                }
                MemberStock memberStock = MemberStock.updateMemberStock(memberStockOutDto.get(),
                        data);
                saveMemberStockPort.SaveMemberStock(memberStock);
                saveStockLogPort.saveStockLog(modelMapper.map(data, StockLog.class),
                        StockLogStatus.BUY, data.getUuid());

                //알림 메세지 전송
                String bodyData =
                        "종목명 : " + reservationBuy.getStockName() + "\n"
                                + "수량 : " + reservationBuy.getAmount() + "개\n"
                                + "총 가격 : " + reservationBuy.getAmount() * reservationBuy.getPrice() + "원\n"
                                + "예약 매수 체결 완료 되었습니다.";
                messageQueuePort.sendNotification(MessageQueueOutDto.TradeStockNotificationDto
                        .builder()
                        .title("예약 매수 체결 완료")
                        .body(bodyData)
                        .uuid(reservationBuy.getUuid())
                        .notificationLogTime(LocalDateTime.now().toString())
                        .build());
            }
        }

        if (!matchSaleStock.isEmpty()) {
            log.info("예약 매도 start = {}", matchSaleStock.get(0).getStockName());
            saveReservationStockPort.concludeSaleStock(matchSaleStock);

            for (ReservationSale reservationSale : matchSaleStock) {
                MemberStockOutDto memberStockOutDto = loadMemberStockPort
                        .LoadMemberStockByUuidAndStockCode(reservationSale.getUuid(),
                                reservationSale.getStockCode())
                        .orElseThrow(() -> new CustomException(
                                BaseResponseCode.SALE_RESERVATION_STOCK_NOTFOUND_ERROR));

                StockUseCase.StockBuySaleDto data = modelMapper.map(reservationSale,
                        StockUseCase.StockBuySaleDto.class);

                MemberStock memberStock = MemberStock.saleMemberStock(memberStockOutDto, data);
                MemberStock savedData = saveMemberStockPort.SaveMemberStock(memberStock);
                StockLog savedLog = saveStockLogPort.saveStockLog(
                        modelMapper.map(data, StockLog.class),
                        StockLogStatus.RESERVATION_SALE, data.getUuid());

                try {
                    messageQueuePort.send(
                            "trade-payment-sale",
                            MessageQueueOutDto.BuyDto
                                    .builder()
                                    .uuid(reservationSale.getUuid())
                                    .price(reservationSale.getPrice() * reservationSale.getAmount())
                                    .build()).get();
                } catch (Exception e) {
                    log.error("Kafka Messaging 도중, 오류 발생");
                    saveMemberStockPort.SaveMemberStock(
                            createBeforeSaleMemberStock(savedData, memberStockOutDto));
                    saveStockLogPort.deleteStockLog(savedLog);
                    throw new CustomException(BaseResponseCode.SALE_STOCK_FAIL_ERROR);
                }

                //알림 메세지 전송
                String bodyData =
                        "종목명 : " + reservationSale.getStockName() + "\n"
                                + "수량 : " + reservationSale.getAmount() + "개\n"
                                + "총 가격 : " + reservationSale.getAmount() * reservationSale.getPrice() + "원\n"
                                + "예약 매도 체결 완료 되었습니다.";
                messageQueuePort.sendNotification(MessageQueueOutDto.TradeStockNotificationDto
                        .builder()
                        .title("예약 매도 체결 완료")
                        .body(bodyData)
                        .uuid(reservationSale.getUuid())
                        .notificationLogTime(LocalDateTime.now().toString())
                        .build());
            }
        }
    }

    private ReservationLogOutDto convertToBuyDto(ReservationBuy buy) {
        ReservationLogOutDto dto = modelMapper.map(buy, ReservationLogOutDto.class);

        dto.setTotalPrice(String.valueOf(buy.getPrice() * buy.getAmount()));
        dto.setStatus(STATUS_BUY);

        return dto;
    }

    private ReservationLogOutDto convertToSaleDto(ReservationSale sale) {
        ReservationLogOutDto dto = modelMapper.map(sale, ReservationLogOutDto.class);

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
