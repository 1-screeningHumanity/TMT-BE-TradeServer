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
    @NoArgsConstructor
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

        @Builder
        private Logs(Long id, LocalDateTime createdAt, String time, String stockName, String price,
                String amount, String totalPrice, String status, String stockCode) {
            this.id = id;
            this.createdAt = createdAt;
            this.time = time;
            this.stockName = stockName;
            this.price = price;
            this.amount = amount;
            this.totalPrice = totalPrice;
            this.status = status;
            this.stockCode = stockCode;
        }
    }
}
