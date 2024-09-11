package ScreeningHumanity.TradeServer.adaptor.in.feignclient;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "Payment", url = "${FEIGN_CLIENT.PAYMENT.URL}")
public interface PaymentFeignClientInterface {

    @GetMapping(value = "/woninfo")
    BaseResponse<RequestDto.WonInfo> searchMemberCash(@RequestHeader(AUTHORIZATION) String accessToken);
}
