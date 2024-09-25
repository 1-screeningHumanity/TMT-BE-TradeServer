package ScreeningHumanity.TradeServer.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import ScreeningHumanity.TradeServer.IntegrationSpringBootTestSupporter;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.MemberStockEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto.WonInfo;
import ScreeningHumanity.TradeServer.application.port.in.dto.StockInDto;
import ScreeningHumanity.TradeServer.application.port.in.dto.StockInDto.Sale;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import ScreeningHumanity.TradeServer.global.common.exception.CustomException;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class StockBuySaleServiceTest extends IntegrationSpringBootTestSupporter {

    @DisplayName("[Success] 주식 즉시 매수를 처리합니다.")
    @Test
    void buyStock() {
        // given
        String uuid = "test";
        String accessToken = "accessToken";
        StockInDto.Buy request =
                createStockInDtoBuy(uuid, 1000L, 1L, "001", "이름1");

        // stubbing
        BDDMockito.given(paymentUseCase.searchMemberCash(accessToken))
                .willReturn(createWonInfo(1000L));
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        stockBuySaleService.buyStock(request, uuid, accessToken);

        // then
        List<MemberStockEntity> lists = memberStockJpaRepository.findAll();
        assertThat(lists).hasSize(1)
                .extracting("uuid", "amount", "totalAmount", "totalPrice", "stockCode")
                .contains(
                        tuple(uuid, 1L, 1L, 1000L, "001")
                );
    }

    @DisplayName("[Success] 주식 즉시 매수를 처리합니다. 기존 데이터가 있다면, 추가합니다.")
    @Test
    void buyStockExistData() {
        // given
        String uuid = "test";
        String accessToken = "accessToken";
        StockInDto.Buy request =
                createStockInDtoBuy(uuid, 1000L, 1L, "001", "이름1");

        MemberStockEntity existData =
                createMemberStockEntity(uuid, 1L, 1000L, 1L, "001", "이름1");
        memberStockJpaRepository.save(existData);

        // stubbing
        BDDMockito.given(paymentUseCase.searchMemberCash(accessToken))
                .willReturn(createWonInfo(1000L));
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        stockBuySaleService.buyStock(request, uuid, accessToken);

        // then
        List<MemberStockEntity> lists = memberStockJpaRepository.findAll();
        assertThat(lists).hasSize(1)
                .extracting("uuid", "amount", "totalAmount", "totalPrice", "stockCode")
                .contains(
                        tuple(uuid, 2L, 2L, 2000L, "001")
                );
    }

    @DisplayName("[Fail] 즉시 매수를 처리하는데, 회원의 보유 현금이 부족하면 매수 되지않고, 오류가 발생합니다.")
    @Test
    void buyStockCashNotEnoughCase1() {
        // given
        String uuid = "test";
        String accessToken = "accessToken";
        StockInDto.Buy request =
                createStockInDtoBuy(uuid, 1000L, 1L, "001", "이름1");

        // stubbing
        BDDMockito.given(paymentUseCase.searchMemberCash(accessToken))
                .willReturn(createWonInfo(900L));
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when // then
        assertThatThrownBy(() -> stockBuySaleService.buyStock(request, uuid, accessToken))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.BUY_STOCK_NOT_ENOUGH_WON);
    }

    @DisplayName("[Fail] 즉시 매수를 처리하는데, 현금 처리 서비스가 문제가 발생하면 처리되지 않습니다")
    @Test
    void buyStockCashNotEnoughCase2() {
        // given
        String uuid = "test";
        String accessToken = "accessToken";
        StockInDto.Buy request =
                createStockInDtoBuy(uuid, 1000L, 1L, "001", "이름1");

        // stubbing
        BDDMockito.given(paymentUseCase.searchMemberCash(accessToken))
                .willReturn(createWonInfo(1000L));
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willThrow(RuntimeException.class);

        // when // then
        assertThatThrownBy(() -> stockBuySaleService.buyStock(request, uuid, accessToken))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.BUY_STOCK_FAIL_ERROR);
    }

    @DisplayName("[Success] 주식 즉시 매도를 처리합니다.")
    @Test
    void saleStock() {
        // given
        StockInDto.Sale request = createStockInDtoSale("test", 1000L, 1L, "001", "주식이름1");
        memberStockJpaRepository.save(
                createMemberStockEntity("test", 1L, 1000L, 1L, "001", "주식이름1"));

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        stockBuySaleService.saleStock(request, "test");

        // then
        List<MemberStockEntity> lists = memberStockJpaRepository.findAll();
        assertThat(lists).hasSize(1)
                .extracting("uuid", "amount", "totalPrice", "totalAmount", "stockCode")
                .contains(
                        tuple("test", 0L, 0L, 0L, "001")
                );

        List<StockLogEntity> logLists = stockLogJpaRepository.findAll();
        assertThat(logLists).hasSize(1)
                .extracting("uuid", "amount", "price", "status", "stockCode", "stockName")
                .contains(
                        tuple("test", 1L, 1000L, StockLogStatus.SALE, "001", "주식이름1")
                );
    }

    @DisplayName("[Success] 즉시 매도를 처리하는데, 매도 후, 남은 수량이 있으면 평단가 계산용 컬럼은 초기화되지 않습니다.")
    @Test
    void saleStockRemainDataCase1() {
        // given
        StockInDto.Sale request = createStockInDtoSale("test", 1000L, 1L, "001", "주식이름1");
        memberStockJpaRepository.save(
                createMemberStockEntity("test", 2L, 2000L, 2L, "001", "주식이름1"));

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        stockBuySaleService.saleStock(request, "test");

        // then
        List<MemberStockEntity> lists = memberStockJpaRepository.findAll();
        assertThat(lists).hasSize(1)
                .extracting("uuid", "amount", "totalPrice", "totalAmount", "stockCode")
                .contains(
                        tuple("test", 1L, 2000L, 2L, "001")
                );
    }

    @DisplayName("[Fail] 매도할 데이터가 없으면, 오류 메세지를 반환합니다.")
    @Test
    void saleStockNotExistMemberStockCase() {
        // given
        StockInDto.Sale request = createStockInDtoSale("test", 1000L, 1L, "001", "주식이름1");

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when // then
        assertThatThrownBy(() -> stockBuySaleService.saleStock(request, "test"))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.SALE_STOCK_NOT_EXIST_ERROR);

        List<StockLogEntity> all = stockLogJpaRepository.findAll();
        assertThat(all).isEmpty();
    }

    @DisplayName("[Fail] 매도할 수량이 부족하면, 오류 메세지를 반환합니다.")
    @Test
    void saleStockNotEnoughMemberStockCase() {
        // given
        StockInDto.Sale request = createStockInDtoSale("test", 1000L, 2L, "001", "주식이름1");
        memberStockJpaRepository.save(
                createMemberStockEntity("test", 1L, 1000L, 1L, "001", "주식이름1"));

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when // then
        assertThatThrownBy(() -> stockBuySaleService.saleStock(request, "test"))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.SALE_STOCK_NEGATIVE_TARGET_ERROR);

        List<StockLogEntity> all = stockLogJpaRepository.findAll();
        assertThat(all).isEmpty();
    }

    @DisplayName("[Fail] 매도를 진행 중, 회원 지갑 서버에서 차감 요청이 응답이 없으면, 요청이 처리되지 않습니다.")
    @Test
    void saleStockCashErrorCase1() {
        // given
        Sale request = createStockInDtoSale("test", 1000L, 1L, "001", "주식이름1");

        memberStockJpaRepository.save(
                createMemberStockEntity("test", 1L, 1000L, 1L, "001", "주식이름1"));

        // stubbing
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willThrow(RuntimeException.class);

        // when // then
        assertThatThrownBy(() -> stockBuySaleService.saleStock(request, "test"))
                .isInstanceOf(CustomException.class)
                .extracting("status")
                .isEqualTo(BaseResponseCode.SALE_STOCK_FAIL_ERROR);
    }

    private MemberStockEntity createMemberStockEntity(String uuid, Long amount, Long totalPrice,
            Long totalAmount, String stockCode, String stockName) {
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

    private WonInfo createWonInfo(Long won) {
        return WonInfo.builder().won(won).build();
    }

    private StockInDto.Sale createStockInDtoSale(String uuid, Long price, Long amount,
            String stockCode, String stockName) {
        return StockInDto.Sale
                .builder()
                .uuid(uuid)
                .price(price)
                .amount(amount)
                .stockCode(stockCode)
                .stockName(stockName)
                .build();
    }

    private StockInDto.Buy createStockInDtoBuy(String uuid, Long price, Long amount,
            String stockCode, String stockName) {
        return StockInDto.Buy
                .builder()
                .uuid(uuid)
                .price(price)
                .amount(amount)
                .stockCode(stockCode)
                .stockName(stockName)
                .build();
    }
}