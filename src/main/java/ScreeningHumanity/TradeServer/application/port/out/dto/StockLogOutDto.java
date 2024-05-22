package ScreeningHumanity.TradeServer.application.port.out.dto;

import ScreeningHumanity.TradeServer.domain.StockLogStatus;
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
public class StockLogOutDto {
    private String time;
    private String stockName;
    private String price;
    private String amount;
    private String totalPrice;
    private StockLogStatus status;
}
