package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.MemberStock;

public interface SaveMemberStockPort {

    MemberStock saveMemberStock(MemberStock memberStock);
}
