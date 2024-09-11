package ScreeningHumanity.TradeServer.adaptor.in.kafka.controller;


import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.application.port.in.dto.ReservationStockInDto;
import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerController {

    private final ReservationStockUseCase reservationStockUseCase;

    private static final Long ALLOWED_MIN = 1L;

    @KafkaListener(topics = "realchart-trade-stockinfo")
    public void reservationStock(String kafkaMessage) {
        RequestDto.RealTimeStockInfo dto = new RequestDto.RealTimeStockInfo();
        ObjectMapper mapper = new ObjectMapper();
        try {
            dto = mapper.readValue(kafkaMessage, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (Boolean.FALSE == checkDurationMin(dto.getDate())) {
            return;
        }

        reservationStockUseCase.doReservationStock(convertToDto(dto));
    }

    private ReservationStockInDto.RealTimeStockInfo convertToDto(RequestDto.RealTimeStockInfo dto) {
        return ReservationStockInDto.RealTimeStockInfo
                .builder()
                .stockCode(dto.getStockCode())
                .price(dto.getPrice())
                .date(dto.getDate())
                .build();
    }

    private Boolean checkDurationMin(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        LocalDateTime requestData = LocalDateTime.parse(dateTime, formatter);

        LocalDateTime nowData = LocalDateTime.now();
        Duration duration = Duration.between(requestData, nowData);
        if (Math.abs(duration.toMinutes()) > ALLOWED_MIN) {
            log.info("{}분 이상 차이나서 체결 동작 하지 않음.", ALLOWED_MIN);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
