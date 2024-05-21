package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.application.port.out.dto.MemberStockDto;
import java.util.Optional;

public interface LoadMemberStockPort {
    Optional<MemberStockDto> LoadMemberStockByUuidAndStockCode(String uuid, Long stockCode);
}
