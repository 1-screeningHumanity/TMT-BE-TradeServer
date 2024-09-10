package ScreeningHumanity.TradeServer.application.port.in.usecase;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import org.springframework.web.bind.annotation.RequestHeader;

public interface PaymentUseCase {
    RequestDto.WonInfo searchMemberCash(String accessToken);
}