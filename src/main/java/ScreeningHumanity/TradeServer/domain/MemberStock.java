package ScreeningHumanity.TradeServer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 보유 주식 Domain
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberStock {

    private Long id;
    private String uuid; //
    private Long amount; //보유 주식 갯수, 사고 팔때마다 변경.
    private Long totalPrice; //총 매수 금액 --금지
    private Long totalAmount; //총 매수 주식 갯수 --금지
    private String stockCode; //종목 코드
    private String stockName; //종목 이름
}
