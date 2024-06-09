package ScreeningHumanity.TradeServer.adaptor.in.kafka.controller;


import ScreeningHumanity.TradeServer.adaptor.in.kafka.dto.RealChartInputDto;
import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerController {

    private final ReservationStockUseCase reservationStockUseCase;
    /**
     * input = 실시간 주식 데이터
     * 예약 매수/매도 체결
     */
    @KafkaListener(topics="realchart-trade-stockinfo")
    public void reservationStock(String kafkaMessage){
        log.info("kafka Message : {}", kafkaMessage);

        RealChartInputDto dto = new RealChartInputDto();
        ObjectMapper mapper = new ObjectMapper();
        try{
            dto = mapper.readValue(kafkaMessage, new TypeReference<>() {});
            log.info("price = {}", dto.getPrice());
            log.info("stockCode = {}", dto.getStockCode());
            log.info("date = {}", dto.getDate());
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        reservationStockUseCase.concludeStock(dto);
    }
}
