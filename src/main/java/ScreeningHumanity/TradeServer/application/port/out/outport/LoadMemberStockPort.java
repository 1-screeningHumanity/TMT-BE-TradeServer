package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.domain.MemberStock;
import java.util.Optional;

public interface LoadMemberStockPort {
    Optional<MemberStock> loadMemberStock(String uuid, String stockCode);
}
