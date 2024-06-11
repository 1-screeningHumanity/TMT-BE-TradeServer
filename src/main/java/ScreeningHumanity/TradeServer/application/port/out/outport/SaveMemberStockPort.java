package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.MemberStock;

public interface SaveMemberStockPort {
    MemberStock SaveMemberStock(MemberStock memberStock);

    void DeleteMemberStock(MemberStock memberStock);
}
