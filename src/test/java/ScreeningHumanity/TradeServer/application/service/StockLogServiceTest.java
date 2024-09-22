package ScreeningHumanity.TradeServer.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import ScreeningHumanity.TradeServer.IntegrationSpringBootTestSupporter;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import ScreeningHumanity.TradeServer.application.port.out.dto.StockLogOutDto;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class StockLogServiceTest extends IntegrationSpringBootTestSupporter {

    @DisplayName("회원의 주식 거래 로그를 모두 불러옵니다. 조회한 순서대로 1번부터 번호를 매겨 반환합니다.")
    @Test
    void loadStockLog() {
        // given
        String uuid = "test";
        Pageable pageable = PageRequest.of(0, 50);

        stockLogJpaRepository.save(createStockLogEntity(uuid, 1L, 1000L, StockLogStatus.BUY, "001", "주식1"));
        stockLogJpaRepository.save(createStockLogEntity(uuid, 2L, 2000L, StockLogStatus.SALE, "002", "주식2"));
        stockLogJpaRepository.save(createStockLogEntity(uuid, 3L, 3000L, StockLogStatus.RESERVATION_BUY, "003", "주식3"));
        stockLogJpaRepository.save(createStockLogEntity(uuid, 4L, 4000L, StockLogStatus.RESERVATION_SALE, "004", "주식4"));

        // stubbing

        // when
        List<StockLogOutDto> result = stockLogService.loadStockLog(pageable, uuid);

        // then
        assertThat(result).hasSize(4)
                .extracting("indexId", "stockName", "price", "status", "totalPrice")
                .containsExactly(
                        tuple(1L, "주식4", "4000", StockLogStatus.RESERVATION_SALE, "16000"),
                        tuple(2L, "주식3", "3000", StockLogStatus.RESERVATION_BUY, "9000"),
                        tuple(3L, "주식2", "2000", StockLogStatus.SALE, "4000"),
                        tuple(4L, "주식1", "1000", StockLogStatus.BUY, "1000")
                );
    }

    @DisplayName("회원의 주식 거래 로그를 모두 불러옵니다. 아무것도 없을때는 빈 결과를 반환합니다.")
    @Test
    void loadStockLogNoDataCase1() {
        // given
        String uuid = "test";
        Pageable pageable = PageRequest.of(0, 50);

        // stubbing

        // when
        List<StockLogOutDto> result = stockLogService.loadStockLog(pageable, uuid);

        // then
        assertThat(result).hasSize(0);
    }

    @DisplayName("페이지네이션을 적용하여, 최대 수량보다 데이터가 많으면, 최대 결과값 만큼 확인됩니다.")
    @Test
    void loadStockLogNoDataCase() {
        // given
        String uuid = "test";
        int pageSize = 2;
        Pageable pageable1 = PageRequest.of(0, pageSize);
        Pageable pageable2 = PageRequest.of(1, pageSize);

        stockLogJpaRepository.save(createStockLogEntity(uuid, 1L, 1000L, StockLogStatus.BUY, "001", "주식1"));
        stockLogJpaRepository.save(createStockLogEntity(uuid, 2L, 2000L, StockLogStatus.SALE, "002", "주식2"));
        stockLogJpaRepository.save(createStockLogEntity(uuid, 3L, 3000L, StockLogStatus.RESERVATION_BUY, "003", "주식3"));

        // stubbing

        // when
        List<StockLogOutDto> result1 = stockLogService.loadStockLog(pageable1, uuid);
        List<StockLogOutDto> result2 = stockLogService.loadStockLog(pageable2, uuid);

        // then
        assertThat(result1).hasSize(2);
        assertThat(result2).hasSize(1);
    }

    private StockLogEntity createStockLogEntity(String uuid, long amount, long price,
            StockLogStatus status, String stockCode, String stockName) {
        return StockLogEntity
                .builder()
                .amount(amount)
                .price(price)
                .status(status)
                .uuid(uuid)
                .stockCode(stockCode)
                .stockName(stockName)
                .build();
    }
}