package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import ScreeningHumanity.TradeServer.IntegrationSpringBootTestSupporter;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationBuyEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationSaleEntity;
import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MemberReservationStockAdaptorTest extends IntegrationSpringBootTestSupporter {

    @DisplayName("[Success] 예약 매수를 저장하고 로드합니다.")
    @Test
    void saveReservationBuyStock() {
        // given
        ReservationBuy request = ReservationBuy
                .builder()
                .id(1L)
                .uuid("testUuid")
                .price(1000L)
                .amount(1L)
                .stockCode("005930")
                .stockName("삼성전자")
                .build();

        // when
        memberReservationStockAdaptor.saveReservationBuyStock(request);
        ReservationBuyEntity findData = reservationBuyJpaRepository.findAll().get(0);

        // then
        assertThat(findData.getUuid()).isEqualTo(request.getUuid());
        assertThat(findData.getPrice()).isEqualTo(request.getPrice());
        assertThat(findData.getAmount()).isEqualTo(request.getAmount());
        assertThat(findData.getStockCode()).isEqualTo(request.getStockCode());
        assertThat(findData.getStockName()).isEqualTo(request.getStockName());
        assertThat(findData.getCreatedAt()).isNotNull();
    }

    @DisplayName("[Success] 예약 매도를 저장하고 로드합니다.")
    @Test
    void saveReservationSaleStock() {
        // given
        ReservationSale request = ReservationSale
                .builder()
                .uuid("testUuid")
                .price(1000L)
                .amount(1L)
                .stockCode("005930")
                .stockName("삼성전자")
                .build();

        // when
        memberReservationStockAdaptor.saveReservationSaleStock(request);
        ReservationSaleEntity findData = reservationSaleJpaRepository.findAll().get(0);

        // then
        assertThat(findData.getUuid()).isEqualTo(request.getUuid());
        assertThat(findData.getPrice()).isEqualTo(request.getPrice());
        assertThat(findData.getAmount()).isEqualTo(request.getAmount());
        assertThat(findData.getStockCode()).isEqualTo(request.getStockCode());
        assertThat(findData.getStockName()).isEqualTo(request.getStockName());
        assertThat(findData.getCreatedAt()).isNotNull();
    }

    @DisplayName("[Success] 예약 매도를 id를 통해 취소하고 취소한 예약 주문의 데이터를 보여줍니다.")
    @Test
    void deleteReservationSaleStock() {
        // given
        ReservationSaleEntity data =
                createReservationSaleEntity("test", 1000L, 1L, "005930", "삼성전자");
        ReservationSaleEntity savedData = reservationSaleJpaRepository.save(data);

        // when
        ReservationSale deletedData = memberReservationStockAdaptor.deleteReservationSaleStock(
                savedData.getId()).orElseThrow();
        Optional<ReservationSaleEntity> findData = reservationSaleJpaRepository.findById(
                savedData.getId());

        // then
        assertThat(deletedData.getId()).isEqualTo(savedData.getId());
        assertThat(deletedData.getUuid()).isEqualTo(savedData.getUuid());
        assertThat(deletedData.getPrice()).isEqualTo(savedData.getPrice());
        assertThat(deletedData.getAmount()).isEqualTo(savedData.getAmount());
        assertThat(deletedData.getStockCode()).isEqualTo(savedData.getStockCode());
        assertThat(deletedData.getStockName()).isEqualTo(savedData.getStockName());
        assertThat(deletedData.getCreatedAt()).isNotNull();

        assertThat(findData).isEmpty();
    }

    @DisplayName("[Success] 예약 매수를 id를 통해 취소하고 취소한 예약 주문의 데이터를 보여줍니다.")
    @Test
    void deleteReservationBuyStock() {
        // given
        ReservationBuyEntity data =
                createReservationBuyEntity("test", 1000L, 1L, "005930", "삼성전자");
        ReservationBuyEntity savedData = reservationBuyJpaRepository.save(data);

        // when
        ReservationBuy deletedData = memberReservationStockAdaptor.deleteReservationBuyStock(
                savedData.getId()).orElseThrow();
        Optional<ReservationBuyEntity> findData = reservationBuyJpaRepository.findById(
                savedData.getId());

        // then
        assertThat(deletedData.getId()).isEqualTo(savedData.getId());
        assertThat(deletedData.getUuid()).isEqualTo(savedData.getUuid());
        assertThat(deletedData.getPrice()).isEqualTo(savedData.getPrice());
        assertThat(deletedData.getAmount()).isEqualTo(savedData.getAmount());
        assertThat(deletedData.getStockCode()).isEqualTo(savedData.getStockCode());
        assertThat(deletedData.getStockName()).isEqualTo(savedData.getStockName());
        assertThat(deletedData.getCreatedAt()).isNotNull();

        assertThat(findData).isEmpty();
    }

    @DisplayName("[Success] 회원의 예약 매수 현황을 조회합니다.")
    @Test
    void loadReservationBuy() {
        // given
        String uuid = "test";
        List<String> stockCodes = List.of("000001", "000002");
        List<String> stockNames = List.of("A", "B");

        for (int i = 0; i < stockCodes.size(); i++) {
            ReservationBuyEntity data = createReservationBuyEntity(uuid, 1000L, 1L,
                    stockCodes.get(i), stockNames.get(i));
            reservationBuyJpaRepository.save(data);
        }

        // when
        List<ReservationBuy> findDataList = memberReservationStockAdaptor.loadReservationBuy(
                uuid);

        // then
        assertThat(findDataList).hasSize(2)
                .extracting("uuid", "price", "amount", "stockCode", "stockName")
                .containsExactlyInAnyOrder(
                        tuple(uuid, 1000L, 1L, stockCodes.get(0), stockNames.get(0)),
                        tuple(uuid, 1000L, 1L, stockCodes.get(1), stockNames.get(1))
                );
    }

    @DisplayName("[Success] 회원의 예약 매도 현황을 조회합니다.")
    @Test
    void loadReservationSale() {
        // given
        String uuid = "test";
        List<String> stockCodes = List.of("000001", "000002");
        List<String> stockNames = List.of("A", "B");

        for (int i = 0; i < stockCodes.size(); i++) {
            ReservationSaleEntity data = createReservationSaleEntity(uuid, 1000L, 1L,
                    stockCodes.get(i), stockNames.get(i));
            reservationSaleJpaRepository.save(data);
        }

        // when
        List<ReservationSale> findDataList = memberReservationStockAdaptor.loadReservationSale(
                uuid);

        // then
        assertThat(findDataList).hasSize(2)
                .extracting("uuid", "price", "amount", "stockCode", "stockName")
                .containsExactlyInAnyOrder(
                        tuple(uuid, 1000L, 1L, stockCodes.get(0), stockNames.get(0)),
                        tuple(uuid, 1000L, 1L, stockCodes.get(1), stockNames.get(1))
                );
    }

    @DisplayName("[Success] 주식 번호와 주식의 현재가로, 예약 매도가 체결될 예약건을 모두 찾습니다.")
    @Test
    void findMatchBuyStock() {
        // given
        String findStockCode = "000001";
        Long findPrice = 1000L;

        List<Long> priceList = List.of(1000L, 1000L, 2000L);
        List<String> stockCodeList = List.of("000001", "000002", "000001");
        List<String> uuidList = List.of("test1", "test2", "test3");

        for (int i = 0; i < uuidList.size(); i++) {
            ReservationBuyEntity data =
                    createReservationBuyEntity(uuidList.get(i), priceList.get(i), 1L, stockCodeList.get(i), "삼성");
            reservationBuyJpaRepository.save(data);
        }

        // when
        List<ReservationBuy> findData = memberReservationStockAdaptor.findMatchBuyStock(
                findStockCode, findPrice);

        // then
        assertThat(findData).hasSize(1)
                .extracting("uuid", "price", "amount", "stockCode", "stockName")
                .contains(
                        tuple(uuidList.get(0), findPrice, 1L, findStockCode, "삼성")
                );
    }

    @DisplayName("[Success] 주식 번호와 주식의 현재가로, 예약 매수가 체결될 예약건을 모두 찾습니다.")
    @Test
    void findMatchSaleStock() {
        // given
        String findStockCode = "000001";
        Long findPrice = 1000L;

        List<Long> priceList = List.of(1000L, 1000L, 2000L);
        List<String> stockCodeList = List.of("000001", "000002", "000001");
        List<String> uuidList = List.of("test1", "test2", "test3");

        for (int i = 0; i < uuidList.size(); i++) {
            ReservationSaleEntity data =
                    createReservationSaleEntity(uuidList.get(i), priceList.get(i), 1L, stockCodeList.get(i), "삼성");
            reservationSaleJpaRepository.save(data);
        }

        // when
        List<ReservationSale> findData = memberReservationStockAdaptor.findMatchSaleStock(
                findStockCode, findPrice);

        // then
        assertThat(findData).hasSize(1)
                .extracting("uuid", "price", "amount", "stockCode", "stockName")
                .contains(
                        tuple(uuidList.get(0), findPrice, 1L, findStockCode, "삼성")
                );
    }

    private ReservationBuyEntity createReservationBuyEntity(
            String uuid, Long price, Long amount, String stockCode, String stockName) {
        return ReservationBuyEntity
                .builder()
                .uuid(uuid)
                .price(price)
                .amount(amount)
                .stockCode(stockCode)
                .stockName(stockName)
                .build();
    }

    private ReservationSaleEntity createReservationSaleEntity(
            String uuid, Long price, Long amount, String stockCode, String stockName) {
        return ReservationSaleEntity
                .builder()
                .uuid(uuid)
                .price(price)
                .amount(amount)
                .stockCode(stockCode)
                .stockName(stockName)
                .build();
    }
}