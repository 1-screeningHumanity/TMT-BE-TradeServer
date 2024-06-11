package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.application.port.out.dto.MessageQueueOutDto;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

public interface MessageQueuePort {

    CompletableFuture<SendResult<String, String>> send(String receiver, Object data);

    void sendNotification(MessageQueueOutDto.TradeStockNotificationDto data);
}
