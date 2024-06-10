package ScreeningHumanity.TradeServer.application.port.out.outport;

public interface NotificationPort {

    void send(String KafkaTopic, Object dto);
}
