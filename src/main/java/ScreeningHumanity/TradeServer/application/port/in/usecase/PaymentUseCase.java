package ScreeningHumanity.TradeServer.application.port.in.usecase;

import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;

public interface PaymentUseCase {

    RequestDto.WonInfo searchMemberCash(String accessToken);
}