package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import ScreeningHumanity.TradeServer.IntegrationSpringBootTestSupporter;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import ScreeningHumanity.TradeServer.domain.StockLog;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class StockLogAdaptorTest extends IntegrationSpringBootTestSupporter {

    @DisplayName("[Success] 주식 거래 로그를 저장합니다.")
    @Test
    void saveStockLog() {
        // given
        String findUuid = "test";
        String findStockCode = "000001";
        StockLog data = StockLog
                .builder()
                .amount(1L)
                .price(1000L)
                .stockCode(findStockCode)
                .stockName("test")
                .build();

        // when
        StockLog savedData = stockLogAdaptor.saveStockLog(data, StockLogStatus.BUY, findUuid);

        // then
        StockLogEntity findData = stockLogJpaRepository.findById(savedData.getId())
                .orElseThrow();
        assertThat(findData).isNotNull()
                .extracting("id", "uuid", "stockCode")
                .contains(savedData.getId(), findUuid, findStockCode);
        assertThat(findData.getCreatedAt()).isNotNull();
    }

    @DisplayName("[Success] 주식 거래 리스트를 조회합니다.")
    @Test
    void loadStockLog() {
        // given
        String findUuid = "test";
        Pageable pageable = PageRequest.of(0, 50);

        StockLogEntity data = createStockLogEntity(1L, 1000L, StockLogStatus.BUY, findUuid,
                "000001", "주식이름");
        StockLogEntity savedData = stockLogJpaRepository.save(data);

        // when
        List<StockLog> stockLogs = stockLogAdaptor.loadStockLog(pageable, findUuid);

        // then
        assertThat(stockLogs).hasSize(1)
                .extracting("id", "uuid", "stockCode")
                .containsExactlyInAnyOrder(
                        tuple(savedData.getId(), findUuid, "000001")
                );

        assertThat(stockLogs.get(0).getCreatedAt()).isNotNull();
    }

    @DisplayName("[Success] 주식 거래 목록이 없으면, 빈 리스트를 반환합니다.")
    @Test
    void loadStockLogPageableCase1() {
        // given
        String findUuid = "test";
        Pageable pageable = PageRequest.of(0, 50);

        StockLogEntity data = createStockLogEntity(1L, 1000L, StockLogStatus.BUY, findUuid,
                "000001", "주식이름");

        // when
        List<StockLog> stockLogs = stockLogAdaptor.loadStockLog(pageable, findUuid);

        // then
        assertThat(stockLogs).hasSize(0);
    }

    private StockLogEntity createStockLogEntity(long amount, long price, StockLogStatus status, String uuid,
            String stockCode, String stockName) {
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