package ScreeningHumanity.TradeServer.application.port.out.dto;

import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
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
}
