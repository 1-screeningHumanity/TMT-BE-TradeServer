package ScreeningHumanity.TradeServer.application.port.out.dto;

import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class StockLogOutDto {

    @JsonProperty(value = "id")
    private Long indexId;
    private String time;
    private String stockName;
    private String price;
    private String amount;
    private String totalPrice;
    private StockLogStatus status;

    @Builder
    private StockLogOutDto(Long indexId, String time, String stockName, String price, String amount,
            String totalPrice, StockLogStatus status) {
        this.indexId = indexId;
        this.time = time;
        this.stockName = stockName;
        this.price = price;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}
