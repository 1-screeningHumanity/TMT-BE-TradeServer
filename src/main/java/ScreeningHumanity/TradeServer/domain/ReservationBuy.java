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
@NoArgsConstructor
public class ReservationBuy {
    private Long id;
    private String uuid;
    private Long price;
    private Long amount;
    private LocalDateTime createdAt;
    private String stockCode;
    private String stockName;

    @Builder
    private ReservationBuy(Long id, String uuid, Long price, Long amount, LocalDateTime createdAt,
            String stockCode, String stockName) {
        this.id = id;
        this.uuid = uuid;
        this.price = price;
        this.amount = amount;
        this.createdAt = createdAt;
        this.stockCode = stockCode;
        this.stockName = stockName;
    }
}
