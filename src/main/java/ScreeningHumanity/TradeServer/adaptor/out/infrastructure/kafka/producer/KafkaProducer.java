package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.kafka.producer;

import ScreeningHumanity.TradeServer.application.port.out.outport.NotificationPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer implements NotificationPort {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     *
     * @param kafkaTopic = receiver
     * @param dto = data
     */
    @Override
    public void send(String KafkaTopic, Object dto) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(dto);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        kafkaTemplate.send(KafkaTopic, jsonInString);
    }
}
