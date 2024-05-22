package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity;

import ScreeningHumanity.TradeServer.domain.StockLog;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import ScreeningHumanity.TradeServer.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock_log")
public class StockLogEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StockLogStatus status;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "stock_code", nullable = false)
    private Long stockCode;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    public static StockLogEntity toEntityFrom(StockLog stockLog, StockLogStatus status, String uuid){
        return StockLogEntity
                .builder()
                .id(stockLog.getId())
                .amount(stockLog.getAmount())
                .price(stockLog.getPrice())
                .status(status)
                .uuid(uuid)
                .stockCode(stockLog.getStockCode())
                .stockName(stockLog.getStockName())
                .build();
    }
}
