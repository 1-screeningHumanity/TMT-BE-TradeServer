package ScreeningHumanity.TradeServer.application.port.out.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class ReservationStockOutDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Setter
    public static class Logs {

        private Long id;
        @JsonIgnore
        private LocalDateTime createdAt;
        private String time;
        private String stockName;
        private String price;
        private String amount;
        private String totalPrice;
        private String status;
        private String stockCode;
    }
}
