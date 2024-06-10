package ScreeningHumanity.TradeServer.application.port.out.outport;

public interface NotificationPort {

    void send(String receiver, Object data);
}
