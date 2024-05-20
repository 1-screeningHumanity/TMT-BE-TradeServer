package ScreeningHumanity.TradeServer.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예약 매도용 Domain
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationSell {

    private Long id;
    private String uuid;
    private Long price;
    private LocalDateTime createdAt;
    private Long stockCode;
}
