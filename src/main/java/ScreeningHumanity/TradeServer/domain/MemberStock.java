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
    private String uuid;
    private Long totalPrice;
    private Long totalAmount;
    private Long stockCode;
}
