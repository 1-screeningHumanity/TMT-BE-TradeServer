package ScreeningHumanity.TradeServer.adaptor.in.feignclient;

import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.application.port.in.usecase.PaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFeignClient implements PaymentUseCase {

    private final PaymentFeignClientInterface feignClient;

    @Override
    public RequestDto.WonInfo searchMemberCash(String accessToken) {
        return feignClient.searchMemberCash(accessToken).result();
    }
}
