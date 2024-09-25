package ScreeningHumanity.TradeServer.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ScreeningHumanity.TradeServer")
public class FeignClientsConfig {

}
