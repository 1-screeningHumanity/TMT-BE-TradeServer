package ScreeningHumanity.TradeServer.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import ScreeningHumanity.TradeServer.IntegrationSpringBootTestSupporter;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.MemberStockEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationBuyEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationSaleEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto.WonInfo;
import ScreeningHumanity.TradeServer.application.port.in.dto.ReservationStockInDto;
import ScreeningHumanity.TradeServer.application.port.in.dto.ReservationStockInDto.RealTimeStockInfo;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationStockOutDto.Logs;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import ScreeningHumanity.TradeServer.global.common.exception.CustomException;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.transaction.annotation.Transactional;

class ReservationStockServiceTest extends IntegrationSpringBootTestSupporter {

    private String accessToken = "abc123";

    @AfterEach
    void tearDown() {
        memberStockJpaRepository.deleteAllInBatch();
        stockLogJpaRepository.deleteAllInBatch();
        reservationBuyJpaRepository.deleteAllInBatch();
        reservationSaleJpaRepository.deleteAllInBatch();
    }

    @DisplayName("[Success] 예약 매수를 등록합니다.")
    @Test
    void buyStock() {
        // given
        ReservationStockInDto.Buy data =
                createReservationStockInDtoBuy("001", 100L, 100L, "test", "주식1");

        // stubbing
        BDDMockito.given(paymentUseCase.searchMemberCash(anyString()))
                .willReturn(WonInfo.builder().won(10000L).build()
                );
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        reservationStockService.buyStock(data, "test", accessToken);

        // then
        ReservationBuyEntity findData = reservationBuyJpaRepository.findAll().get(0);
        assertThat(findData).isNotNull()
                .extracting("uuid", "price", "amount", "stockCode", "stockName")
                .contains("test", 100L, 100L, "001", "주식1");
    }

    @DisplayName("[Fail] 예약 매수을 등록하는 중, 회원의 금액이 부족하면 처리되지 않고 오류 메세지를 반환합니다.")
    @Test
    void buyStockWonNotEnoughCase() {
        // given
        ReservationStockInDto.Buy data =
                createReservationStockInDtoBuy("001", 100L, 100L, "test", "주식1");

        // stubbing
        BDDMockito.given(paymentUseCase.searchMemberCash(anyString()))
                .willReturn(WonInfo.builder().won(1L).build()
                );

        // when // then
        assertThatThrownBy(() -> reservationStockService.buyStock(data, "test", accessToken))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.BUY_STOCK_NOT_ENOUGH_WON);
    }

    @DisplayName("[Fail] 예약 매수를 등록하는 중, 금액 차감 요청이 실패되면 처리되지 않고 오류 메세지를 반환합니다.")
    @Test
    void buyStockMessageErrorCase() {
        // given
        ReservationStockInDto.Buy data =
                createReservationStockInDtoBuy("001", 100L, 100L, "test", "주식1");

        // stubbing
        BDDMockito.given(paymentUseCase.searchMemberCash(anyString()))
                .willReturn(WonInfo.builder().won(10000L).build()
                );
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willThrow(RuntimeException.class);

        // when // then
        assertThatThrownBy(() -> reservationStockService.buyStock(data, "test", accessToken))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.BUY_RESERVATION_STOCK_FAIL_ERROR);
    }

    @DisplayName("[Success] 예약 매도를 등록합니다.")
    @Test
    void saleStock() {
        // given
        String uuid = "test";
        String stockName = "주식1";
        String stockCode = "001";

        ReservationStockInDto.Sale data =
                createReservationStockInDtoSale(stockCode, 1000L, 1L, uuid, stockName);
        MemberStockEntity stockData =
                createMemberStockEntity(uuid, 1L, 1000L, 1000L, stockCode, stockName);
        memberStockJpaRepository.save(stockData);

        // when
        reservationStockService.saleStock(data, "test");

        // then
        ReservationSaleEntity findData = reservationSaleJpaRepository.findAll().get(0);
        assertThat(findData).isNotNull()
                .extracting("uuid", "price", "amount", "stockCode", "stockName")
                .contains(uuid, 1000L, 1L, stockCode, stockName);
    }

    @DisplayName("[Fail] 예약 매도를 등록하는 중, 보유중인 주식이 없으면, 오류를 반환합니다.")
    @Test
    void saleStockMemberStockNotExistCase() {
        // given
        String uuid = "test";
        String stockName = "주식1";
        String stockCode = "001";

        ReservationStockInDto.Sale data = createReservationStockInDtoSale(stockCode, 1000L, 1L, uuid, stockName);

        // when // then
        assertThatThrownBy(() -> reservationStockService.saleStock(data, "test"))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.SALE_RESERVATION_STOCK_NOTFOUND_ERROR);
    }

    @DisplayName("[Fail] 예약 매도를 등록하는 중, 보유중인 주식의 수량이 매도 수량보다 적으면, 오류를 반환합니다.")
    @Test
    void saleStockMemberStockNotEnoughCase() {
        // given
        String uuid = "test";
        String stockName = "주식1";
        String stockCode = "001";

        ReservationStockInDto.Sale data =
                createReservationStockInDtoSale(stockCode, 1000L, 2L, uuid, stockName);
        MemberStockEntity stockData = createMemberStockEntity(uuid, 1L, 1000L, 1000L, stockCode, stockName);
        memberStockJpaRepository.save(stockData);

        // when // then
        assertThatThrownBy(() -> reservationStockService.saleStock(data, "test"))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.SALE_RESERVATION_ALL_STOCK_REGISTERED);
    }

    @DisplayName("[Fail] 예약 매도를 등록하는 중, 이미 예약된 매도 수량이 많으면 등록할 수 없습니다.")
    @Test
    void saleStockMemberStockNotEnoughCase2() {
        // given
        String uuid = "test";
        String stockName = "주식1";
        String stockCode = "001";

        ReservationStockInDto.Sale data =
                createReservationStockInDtoSale(stockCode, 1000L, 1L, uuid, stockName);

        MemberStockEntity stockData = createMemberStockEntity(uuid, 1L, 1000L, 1000L, stockCode, stockName);
        memberStockJpaRepository.save(stockData);
        reservationStockService.saleStock(data, uuid);

        // when // then
        assertThatThrownBy(() -> reservationStockService.saleStock(data, uuid))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.SALE_RESERVATION_ALL_STOCK_REGISTERED);
    }

    @DisplayName("[Success] 실제 주식 데이터를 기반으로 예약 매수를 채결 합니다.")
    @Test
    void doReservationStockOnlyBuy() {
        // given
        String stockCode = "001";
        Long price = 1000L;
        Long amount = 10L;
        String uuid = "test";
        String stockName = "이름1";

        ReservationStockInDto.RealTimeStockInfo request =
                createRealTimeStockInfo(stockCode, price);

        ReservationBuyEntity data = createReservationBuyEntity(
                uuid, price, amount, stockCode, stockName
        );
        reservationBuyJpaRepository.save(data);

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        reservationStockService.doReservationStock(request);

        // then
//        MemberStockEntity findData = memberStockJpaRepository.findAllByUuidAndStockCode(
//                uuid, stockCode).orElseThrow();
        MemberStockEntity findData = memberStockJpaRepository.findAll().get(0);
        List<ReservationBuyEntity> findReservationData = reservationBuyJpaRepository.findAll();
        List<StockLogEntity> findStockLog = stockLogJpaRepository.findAll();

        assertThat(findData).isNotNull()
                .extracting("amount", "totalPrice", "totalAmount", "stockCode", "stockName")
                .contains(amount, 10000L, amount, stockCode, stockName);

        assertThat(findReservationData).isEmpty();

        assertThat(findStockLog).hasSize(1)
                .extracting("amount", "price", "status", "uuid", "stockCode", "stockName")
                .contains(
                        tuple(amount, price, StockLogStatus.RESERVATION_BUY, uuid, stockCode, stockName)
                );
    }

    @DisplayName("[Success] 예약 매수를 체결하고, 기존에 있던 주식 데이터에 추가가 됩니다.")
    @Test
    void doReservationStockOnlyBuyCase2() {
        // given
        String stockCode = "001";
        Long price = 1000L;
        Long amount = 10L;
        String uuid = "test";
        String stockName = "이름1";

        ReservationStockInDto.RealTimeStockInfo request =
                createRealTimeStockInfo(stockCode, price);

        ReservationBuyEntity data = createReservationBuyEntity(uuid, price, amount, stockCode, stockName);
        reservationBuyJpaRepository.save(data);

        MemberStockEntity alreadyData = createMemberStockEntity(uuid, amount, amount * price, amount, stockCode, stockName);
        memberStockJpaRepository.save(alreadyData);

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        reservationStockService.doReservationStock(request);

        // then
//        MemberStockEntity findData = memberStockJpaRepository.findAllByUuidAndStockCode(uuid, stockCode).orElseThrow();
        MemberStockEntity findData = memberStockJpaRepository.findAll().get(0);
        assertThat(findData).isNotNull()
                .extracting("amount", "totalPrice", "totalAmount", "stockCode", "stockName")
                .contains(amount + amount, 10000L + 10000L, amount + amount, stockCode, stockName);

        List<ReservationBuyEntity> findReservationData = reservationBuyJpaRepository.findAll();
        assertThat(findReservationData).isEmpty();
    }

    @DisplayName("[Fail] 예약 매수를 체결하는 중, 현금 업데이트에 실패하면 예약 매수가 취소됩니다.")
    @Test
    void doReservationStockOnlyBuyCase3() {
        // given
        String stockCode = "001";
        Long price = 1000L;
        Long amount = 10L;
        String uuid = "test";
        String stockName = "이름1";

        ReservationStockInDto.RealTimeStockInfo request =
                createRealTimeStockInfo(stockCode, price);

        ReservationBuyEntity data = createReservationBuyEntity(uuid, price, amount, stockCode, stockName);
        reservationBuyJpaRepository.save(data);

        MemberStockEntity alreadyData = createMemberStockEntity(uuid, amount, amount * price, amount, stockCode, stockName);
        memberStockJpaRepository.save(alreadyData);

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willThrow(RuntimeException.class);

        // when // then
        assertThatThrownBy(() -> reservationStockService.doReservationStock(request))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.BUY_RESERVATION_STOCK_CANCEL_FAIL_ERROR);

//        MemberStockEntity findData = memberStockJpaRepository.findAllByUuidAndStockCode(uuid, stockCode).orElseThrow();
        MemberStockEntity findData = memberStockJpaRepository.findAll().get(0);
        //롤백검증
        assertThat(findData).isNotNull()
                .extracting("amount", "totalPrice", "totalAmount", "stockCode", "stockName")
                .contains(amount, 10000L, amount, stockCode, stockName);

        List<ReservationBuyEntity> findReservationData = reservationBuyJpaRepository.findAll();
        assertThat(findReservationData).hasSize(1);
    }

    @DisplayName("[Success] 실제 주식 데이터를 기반으로 예약 매도를 채결 합니다.")
    @Test
    void doReservationStockOnlySale() {
        // given
        String stockCode = "001";
        Long price = 1000L;
        Long amount = 10L;
        String uuid = "test";
        String stockName = "이름1";

        ReservationStockInDto.RealTimeStockInfo request =
                createRealTimeStockInfo(stockCode, price);

        MemberStockEntity stockData = createMemberStockEntity(uuid, amount, 1000L, 1000L, stockCode, stockName);
        memberStockJpaRepository.save(stockData);

        ReservationSaleEntity data = createReservationSaleEntity(uuid, price, amount-1, stockCode, stockName);
        reservationSaleJpaRepository.save(data);

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        reservationStockService.doReservationStock(request);

        // then
//        MemberStockEntity findData = memberStockJpaRepository.findAllByUuidAndStockCode(uuid, stockCode).orElseThrow();
        MemberStockEntity findData = memberStockJpaRepository.findAll().get(0);
        List<ReservationSaleEntity> findReservationData = reservationSaleJpaRepository.findAll();
        List<StockLogEntity> findLogData = stockLogJpaRepository.findAll();

        assertThat(findData).isNotNull()
                .extracting("amount", "totalPrice", "totalAmount", "stockCode", "stockName")
                .contains(1L, 1000L, 1000L, stockCode, stockName);

        assertThat(findReservationData).isEmpty();

        assertThat(findLogData).hasSize(1)
                .extracting("amount", "price", "status", "uuid", "stockCode", "stockName")
                .contains(
                        tuple(amount-1, price, StockLogStatus.RESERVATION_SALE, uuid, stockCode, stockName)
                );
    }

    @DisplayName("[Fail] 예약 매도를 체결을 진행 중, 회원 서버에서 응답이 없는경우, 체결이 되지 않습니다.")
    @Test
    void doReservationStockOnlySalePaymentServerErrorCase() {
        // given
        String stockCode = "001";
        Long price = 1000L;
        Long amount = 10L;
        String uuid = "test";
        String stockName = "이름1";

        ReservationStockInDto.RealTimeStockInfo request =
                createRealTimeStockInfo(stockCode, price);

        MemberStockEntity stockData = createMemberStockEntity(uuid, amount, 1000L, 1000L, stockCode, stockName);
        memberStockJpaRepository.save(stockData);

        ReservationSaleEntity data = createReservationSaleEntity(uuid, price, amount-1, stockCode, stockName);
        reservationSaleJpaRepository.save(data);

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willThrow(RuntimeException.class);

        // when // then
        assertThatThrownBy(() -> reservationStockService.doReservationStock(request))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.SALE_STOCK_FAIL_ERROR);

//        MemberStockEntity findData = memberStockJpaRepository.findAllByUuidAndStockCode(
//                uuid, stockCode).orElseThrow();
        MemberStockEntity findData = memberStockJpaRepository.findAll().get(0);
        assertThat(findData).isNotNull()
                .extracting("uuid", "amount", "totalPrice", "totalAmount", "stockCode")
                .contains(uuid, amount, 1000L, 1000L, stockCode);

        List<ReservationSaleEntity> findReservationData = reservationSaleJpaRepository.findAll();
        assertThat(findReservationData).hasSize(1);
    }

    @DisplayName("[Success] 예약 매수를 체결 후, 주식 수량이 없는경우, 총구매 수량과 총 구매 금액은 0으로 초기화 됩니다.")
    @Test
    void doReservationStockOnlySaleCase2() {
        // given
        String stockCode = "001";
        Long price = 1000L;
        Long amount = 10L;
        String uuid = "test";
        String stockName = "이름1";

        ReservationStockInDto.RealTimeStockInfo request =
                createRealTimeStockInfo(stockCode, price);

        MemberStockEntity stockData = createMemberStockEntity(uuid, amount, 1000L, 1000L, stockCode, stockName);
        memberStockJpaRepository.save(stockData);

        ReservationSaleEntity data = createReservationSaleEntity(uuid, price, amount, stockCode, stockName);
        reservationSaleJpaRepository.save(data);

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        reservationStockService.doReservationStock(request);

        // then
//        MemberStockEntity findData = memberStockJpaRepository.findAllByUuidAndStockCode(uuid, stockCode).orElseThrow();
        MemberStockEntity findData = memberStockJpaRepository.findAll().get(0);
        List<ReservationSaleEntity> findReservationData = reservationSaleJpaRepository.findAll();
        List<StockLogEntity> findLogData = stockLogJpaRepository.findAll();

        assertThat(findData).isNotNull()
                .extracting("amount", "totalPrice", "totalAmount", "stockCode", "stockName")
                .contains(0L, 0L, 0L, stockCode, stockName);

        assertThat(findReservationData).isEmpty();

        assertThat(findLogData).hasSize(1)
                .extracting("amount", "price", "status", "uuid", "stockCode", "stockName")
                .contains(
                        tuple(amount, price, StockLogStatus.RESERVATION_SALE, uuid, stockCode, stockName)
                );
    }

    @DisplayName("[Success] 예약 매매를 현황을 조회합니다. 최신에 등록된 순서부터 정리하여 반환합니다.")
    @Test
    void buySaleLog() {
        // given
        String uuid = "test";
        reservationSaleJpaRepository.save(createReservationSaleEntity(uuid, 1000L, 1L, "001", "주식1"));
        reservationBuyJpaRepository.save(createReservationBuyEntity(uuid, 2000L, 2L, "002", "주식2"));
        reservationSaleJpaRepository.save(createReservationSaleEntity(uuid, 3000L, 3L, "003", "주식3"));

        // stubbing

        // when
        List<Logs> logs = reservationStockService.buySaleLog(uuid);

        // then
        assertThat(logs).hasSize(3)
                .extracting("price", "amount", "stockCode", "status")
                .containsExactly(
                        tuple("3000", "3", "003", ReservationStockService.STATUS_SALE),
                        tuple("2000", "2", "002", ReservationStockService.STATUS_BUY),
                        tuple("1000", "1", "001", ReservationStockService.STATUS_SALE)
                );
    }

    @DisplayName("[Success] 예약 매도 건을 취소합니다. ")
    @Test
    void cancelReservationSaleStock() {
        // given
        ReservationSaleEntity savedData1 = reservationSaleJpaRepository.save(
                createReservationSaleEntity("test", 1000L, 1L, "001", "이름1"));
        ReservationSaleEntity savedData2 = reservationSaleJpaRepository.save(
                createReservationSaleEntity("test", 2000L, 2L, "002", "이름2"));

        // when
        reservationStockService.cancelReservationSaleStock(savedData1.getId(), false);

        // then
        List<ReservationSaleEntity> lists = reservationSaleJpaRepository.findAll();
        assertThat(lists).hasSize(1)
                .extracting("uuid", "price", "amount", "stockCode", "stockName")
                .contains(
                        tuple("test", 2000L, 2L, "002", "이름2")
                );

        BDDMockito.verify(messageQueuePort, BDDMockito.never()).sendNotification(any());
    }

    @DisplayName("[Fail] 잘못된 예약 매도건을 취소하면 오류 메세지를 반환합니다.")
    @Test
    void cancelReservationSaleStockErrorCase() {
        // given
        ReservationSaleEntity savedData = reservationSaleJpaRepository.save(
                createReservationSaleEntity("test", 1000L, 1L, "001", "이름1"));

        // when // then
        assertThatThrownBy(() -> reservationStockService.cancelReservationSaleStock(savedData.getId() + 1, false))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.DELETE_RESERVATION_SALE_STOCK_ERROR);
    }

    @DisplayName("[Success] 예약 매도를 취소하면 메세지를 보낼 수 있습니다.")
    @Test
    void cancelReservationSaleStockWithSendMessage() {
        // given
        ReservationSaleEntity savedData1 = reservationSaleJpaRepository.save(
                createReservationSaleEntity("test", 1000L, 1L, "001", "이름1"));

        // when
        reservationStockService.cancelReservationSaleStock(savedData1.getId(), true);

        // then
        BDDMockito.verify(messageQueuePort).sendNotification(any());
    }

    @DisplayName("[Success] 예약 매수 건을 취소합니다.")
    @Test
    void cancelReservationBuyStock() {
        // given
        ReservationBuyEntity savedData1 = reservationBuyJpaRepository.save(
                createReservationBuyEntity("test", 1000L, 1L, "001", "이름1")
        );
        ReservationBuyEntity savedData2 = reservationBuyJpaRepository.save(
                createReservationBuyEntity("test", 2000L, 2L, "002", "이름2")
        );

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        reservationStockService.cancelReservationBuyStock(savedData1.getId(), false);

        // then
        List<ReservationBuyEntity> lists = reservationBuyJpaRepository.findAll();
        assertThat(lists).hasSize(1)
                .extracting("uuid", "price", "amount", "stockCode", "stockName")
                .contains(tuple("test", 2000L, 2L, "002", "이름2"));

        BDDMockito.verify(messageQueuePort, BDDMockito.never()).sendNotification(any());
    }

    @DisplayName("[Success] 예약 매수를 취소하면 메세지를 보낼 수 있습니다.")
    @Test
    void cancelReservationBuyStockWithSendMessage() {
        // given
        ReservationBuyEntity savedData1 = reservationBuyJpaRepository.save(
                createReservationBuyEntity("test", 1000L, 1L, "001", "이름1")
        );

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        reservationStockService.cancelReservationBuyStock(savedData1.getId(), true);

        // then
        BDDMockito.verify(messageQueuePort).sendNotification(any());
    }

    @DisplayName("[Fail] 현금 업데이트에 실패하면, 예약 매수 취소를 롤백합니다.")
    @Test
    void cancelReservationBuyStockCashErrorCase1() {
        // given
        ReservationBuyEntity savedData1 = reservationBuyJpaRepository.save(
                createReservationBuyEntity("test", 1000L, 1L, "001", "이름1")
        );

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willThrow(RuntimeException.class);

        // when // then
        assertThatThrownBy(() -> reservationStockService.cancelReservationBuyStock(savedData1.getId(), false))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.BUY_RESERVATION_STOCK_CANCEL_FAIL_ERROR);
    }

    @DisplayName("[Fail] 잘못된 예약 매수건을 취소하면, 오류 메세지를 반환합니다.")
    @Test
    void cancelReservationBuyStockErrorCase() {
        // given

        // when // then
        assertThatThrownBy( () -> reservationStockService.cancelReservationBuyStock(1L, false))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.DELETE_RESERVATION_BUY_STOCK_ERROR);
    }

    private ReservationBuyEntity createReservationBuyEntity(String uuid, Long price, Long amount, String stockCode, String stockName
    ) {
        return ReservationBuyEntity
                .builder()
                .uuid(uuid)
                .price(price)
                .amount(amount)
                .stockCode(stockCode)
                .stockName(stockName)
                .build();
    }

    private ReservationSaleEntity createReservationSaleEntity(String uuid, Long price, Long amount, String stockCode, String stockName
    ) {
        return ReservationSaleEntity
                .builder()
                .uuid(uuid)
                .price(price)
                .amount(amount)
                .stockCode(stockCode)
                .stockName(stockName)
                .build();
    }

    private RealTimeStockInfo createRealTimeStockInfo(String stockCode, Long price) {
        return RealTimeStockInfo
                .builder()
                .stockCode(stockCode)
                .price(price)
                .date(LocalDateTime.of(2024,1,1,10,0).toString())
                .build();
    }

    private ReservationStockInDto.Buy createReservationStockInDtoBuy(String stockCode, Long price, Long amount, String uuid, String stockName
    ) {
        return ReservationStockInDto.Buy
                .builder()
                .stockCode(stockCode)
                .price(price)
                .amount(amount)
                .uuid(uuid)
                .stockName(stockName)
                .build();
    }

    private ReservationStockInDto.Sale createReservationStockInDtoSale(String stockCode, Long price, Long amount, String uuid, String stockName
    ) {
        return ReservationStockInDto.Sale
                .builder()
                .stockCode(stockCode)
                .price(price)
                .amount(amount)
                .uuid(uuid)
                .stockName(stockName)
                .build();
    }

    private MemberStockEntity createMemberStockEntity(String uuid, Long amount, Long totalPrice, Long totalAmount, String stockCode, String stockName) {
        return MemberStockEntity
                .builder()
                .uuid(uuid)
                .amount(amount)
                .totalPrice(totalPrice)
                .totalAmount(totalAmount)
                .stockCode(stockCode)
                .stockName(stockName)
                .build();
    }
}