package ScreeningHumanity.TradeServer.application.service.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import ScreeningHumanity.TradeServer.IntegrationSpringBootTestSupporter;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.MemberStockEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationBuyEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationSaleEntity;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto.WonInfo;
import ScreeningHumanity.TradeServer.application.port.in.dto.ReservationStockInDto;
import ScreeningHumanity.TradeServer.global.common.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

public class ReservationStockServiceConcurrentTest extends IntegrationSpringBootTestSupporter {

    @AfterEach
    void tearDown() {
        memberStockJpaRepository.deleteAllInBatch();
        reservationSaleJpaRepository.deleteAllInBatch();
        reservationBuyJpaRepository.deleteAllInBatch();
        stockLogJpaRepository.deleteAllInBatch();
    }

    @DisplayName("동시에 같은 회원에게 예약 매수를 걸어어도 문제가 없어야 한다.")
    @Test
    void buyStockConcurrent() throws InterruptedException, ExecutionException {
        // given
        ReservationStockInDto.Buy request =
                createReservationStockInDtoBuy("001", 1000L, 1L, "test", "이름1");
        String accessToken = "test";
        int threadCount = 10;

        // stubbing
        BDDMockito.given(paymentUseCase.searchMemberCash(accessToken))
                .willReturn(WonInfo.builder().won(10000L).build());
        BDDMockito.given(messageQueuePort.send(anyString(), any()))
                .willReturn(CompletableFuture.completedFuture(null));

        // when
        executeInThreads(threadCount, () -> {
            reservationStockService.buyStock(request, "test", accessToken);
        });

        // then
        List<ReservationBuyEntity> lists = reservationBuyJpaRepository.findAll();
        assertThat(lists).hasSize(threadCount)
                .extracting("uuid")
                .contains("test");
    }

    @DisplayName("동시에 같은 회원에게 예약 매도를 걸어어도 문제가 없어야 한다.")
    @Test
    void saleStockConcurrent() throws InterruptedException, ExecutionException {
        // given
        int threadCount = 10;
        ReservationStockInDto.Sale request =
                createReservationStockInDtoSale("001", 1000L, 1L, "test", "이름1");

        memberStockJpaRepository.save(createMemberStockEntity("test", 100L, 10000L, 100L,
                "001", "이름1"));

        // when
        executeInThreads(threadCount, () -> {
            reservationStockService.saleStock(request, "test");
        });

        // then
        List<ReservationSaleEntity> lists = reservationSaleJpaRepository.findAll();
        assertThat(lists).hasSize(threadCount)
                .extracting("uuid")
                .contains("test");
    }

    @DisplayName("동시에 같은 회원에게 예약 가능한 수량보다 많이 요청이 접수되면, 예약 불가 메세지가 반영되어야한다.")
    @Test
    void saleStockConcurrentOverRequestCase1() throws InterruptedException, ExecutionException {
        // given
        int threadCount = 10;
        ReservationStockInDto.Sale request =
                createReservationStockInDtoSale("001", 1000L, 1L, "test", "이름1");

        memberStockJpaRepository.save(createMemberStockEntity("test", 9L, 10000L, 9L,
                "001", "이름1"));

        // when // then
        assertThatThrownBy(() -> executeInThreads(threadCount, () -> {
            reservationStockService.saleStock(request, "test");
        }))
                .isInstanceOf(CustomException.class);
    }

    private void executeInThreads(int threadCount, Runnable task)
            throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 1; i <= threadCount; i++) {
            Future<?> submit = executorService.submit(task);
            futures.add(submit);
        }

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                // ExecutionException에서 원래 발생한 예외를 그대로 다시 던짐
                throw (RuntimeException) e.getCause(); // RuntimeException이나 적절한 예외로 캐스팅하여 다시 던짐
            }
        }
    }

    private ReservationStockInDto.Buy createReservationStockInDtoBuy(String stockCode, Long price,
            Long amount, String uuid, String stockName
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

    private ReservationStockInDto.Sale createReservationStockInDtoSale(String stockCode, Long price,
            Long amount, String uuid, String stockName
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
}
