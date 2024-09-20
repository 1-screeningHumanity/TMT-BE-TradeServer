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
@NoArgsConstructor
public class StockLog {
    private Long id;
    private Long amount;
    private Long price;
    private StockLogStatus status;
    private String uuid;
    private String stockCode;
    private LocalDateTime createdAt;
    private String stockName;

    @Builder
    public StockLog(String uuid, Long id, Long amount, Long price, StockLogStatus status,
            String stockCode, LocalDateTime createdAt, String stockName) {
        this.uuid = uuid;
        this.id = id;
        this.amount = amount;
        this.price = price;
        this.status = status;
        this.stockCode = stockCode;
        this.createdAt = createdAt;
        this.stockName = stockName;
    }
}
