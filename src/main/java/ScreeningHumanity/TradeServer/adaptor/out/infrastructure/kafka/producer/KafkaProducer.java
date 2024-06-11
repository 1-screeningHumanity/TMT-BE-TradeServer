package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.kafka.producer;

import ScreeningHumanity.TradeServer.application.port.out.dto.MessageQueueOutDto;
import ScreeningHumanity.TradeServer.application.port.out.outport.MessageQueuePort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer implements MessageQueuePort {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * @param kafkaTopic = receiver
     * @param dto        = data
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<SendResult<String, String>> send(String KafkaTopic, Object dto) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(dto);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        return kafkaTemplate.send(KafkaTopic, jsonInString);
    }

    @Override
    public void sendNotification(MessageQueueOutDto.TradeStockNotificationDto data) {
        String KafkaTopic = "trade-notification-alarm";
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(data);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        kafkaTemplate.send(KafkaTopic, jsonInString);
    }
}
