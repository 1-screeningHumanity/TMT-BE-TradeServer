package ScreeningHumanity.TradeServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableDiscoveryClient //별도의 Config로 분기 처리. 사유 : Mvc Test or SpringBootTest 시, Bean등록 처리됨.
//@EnableJpaAuditing //별도의 Config로 분기 처리. 사유 : Mvc Test or SpringBootTest 시, Bean등록 처리됨.
//@EnableFeignClients //별도의 Config로 분기 처리. 사유 : Mvc Test or SpringBootTest 시, Bean등록 처리됨.
public class TradeServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeServerApplication.class, args);
	}

}
