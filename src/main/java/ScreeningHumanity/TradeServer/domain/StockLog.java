package ScreeningHumanity.TradeServer.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주식 로그용 Domain
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockLog {

    private Long id;
    private Long amount;
    private Long price;
    private LocalDateTime createdAt;
    private StockLogStatus status;
    private String uuid;
    private Long stockCode;
}
