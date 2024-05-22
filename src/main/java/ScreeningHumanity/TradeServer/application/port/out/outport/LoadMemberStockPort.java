package ScreeningHumanity.TradeServer.application.port.out.outport;

import ScreeningHumanity.TradeServer.application.port.out.dto.MemberStockOutDto;
import java.util.Optional;

public interface LoadMemberStockPort {
    Optional<MemberStockOutDto> LoadMemberStockByUuidAndStockCode(String uuid, Long stockCode);
}
