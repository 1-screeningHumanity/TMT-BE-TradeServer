package ScreeningHumanity.TradeServer.adaptor.in.feignclient.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class RequestVo {

    @Getter
    public static class WonInfo {

        @NotNull
        private Long won;
    }
}
