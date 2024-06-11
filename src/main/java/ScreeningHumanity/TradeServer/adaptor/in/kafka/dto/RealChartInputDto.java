package ScreeningHumanity.TradeServer.adaptor.in.kafka.dto;

import lombok.Getter;

@Getter
public class RealChartInputDto {
    public String stockCode;
    public Long price;
    public String date;
}
