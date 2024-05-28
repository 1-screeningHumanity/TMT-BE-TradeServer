package ScreeningHumanity.TradeServer.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예약 매수용 Domain
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationBuy {
    private Long id;
    private String uuid;
    private Long price;
    private Long amount;
    private LocalDateTime createdAt;
    private String stockCode;
    private String stockName;
}
