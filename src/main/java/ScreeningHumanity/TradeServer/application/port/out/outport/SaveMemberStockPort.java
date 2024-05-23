package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.MemberStock;

public interface SaveMemberStockPort {
    void SaveMemberStock(MemberStock memberStock);
}
