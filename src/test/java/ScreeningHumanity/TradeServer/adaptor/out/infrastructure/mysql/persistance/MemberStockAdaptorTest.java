package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import static org.assertj.core.api.Assertions.assertThat;

import ScreeningHumanity.TradeServer.IntegrationSpringBootTestSupporter;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.MemberStockEntity;
import ScreeningHumanity.TradeServer.domain.MemberStock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MemberStockAdaptorTest extends IntegrationSpringBootTestSupporter {

    @DisplayName("[Success] 주식 번호로, 회원이 가지고있는 주식을 검색합니다.")
    @Test
    void loadMemberStock() {
        // given
        String findUuid = "test1";
        String findStockCode = "00001";

        MemberStockEntity data1 = createMemberStockEntity(
                findUuid, 1L, 10000L, 1L, findStockCode, "삼성");

        memberStockJpaRepository.save(data1);

        // when
        MemberStock findData = memberStockAdaptor.loadMemberStock(findUuid,
                findStockCode).orElseThrow();

        // then
        assertThat(findData.getId()).isNotNull();
        assertThat(findData.getUuid()).isEqualTo(findUuid);
        assertThat(findData.getStockCode()).isEqualTo(findStockCode);
    }

    @DisplayName("[Success] 회원의 새로운 주식을 저장합니다.")
    @Test
    void saveMemberStock() {
        // given
        String findUuid = "test1";
        String findStockCode = "00001";
        MemberStock data = MemberStock
                .builder()
                .uuid(findUuid)
                .amount(1L)
                .totalPrice(1000L)
                .totalAmount(1L)
                .stockCode(findStockCode)
                .stockName("이름")
                .build();

        // when
        MemberStock savedData = memberStockAdaptor.saveMemberStock(data);

        // then
        MemberStockEntity findData = memberStockJpaRepository.findById(savedData.getId())
                .orElseThrow();

        assertThat(findData)
                .extracting("id", "uuid", "stockCode")
                .contains(savedData.getId(), findUuid, findStockCode);
    }


    private MemberStockEntity createMemberStockEntity(
            String uuid, Long amount, Long totalPrice, Long totalAmount, String stockCode,
            String stockName
    ) {
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