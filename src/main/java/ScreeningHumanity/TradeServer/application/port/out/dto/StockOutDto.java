package ScreeningHumanity.TradeServer.application.port.out.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class StockOutDto {

    public static class Load {

        private Long id;
        private String uuid;
        private Long amount; //보유 주식 갯수, 사고 팔때마다 변경.
        private Long totalPrice; //총 매수 금액 --금지
        private Long totalAmount; //총 매수 주식 갯수 --금지
        private String stockCode; //종목 코드
        private String stockName; //종목 이름
    }
}
