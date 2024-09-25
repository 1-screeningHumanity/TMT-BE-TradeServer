package ScreeningHumanity.TradeServer.adaptor.in.web.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.application.port.in.usecase.StockLogUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.StockLogOutDto;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import ScreeningHumanity.TradeServer.global.common.token.DecodingToken;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class StockLogController {

    private final StockLogUseCase stockLogUseCase;
    private final DecodingToken decodingToken;

    @GetMapping("/trade-lists")
    public BaseResponse<List<StockLogOutDto>> stockLog(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "50", required = false) int size,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        Pageable pageable = PageRequest.of(page, size);
        String uuid = decodingToken.getUuid(accessToken);

        return new BaseResponse<>(stockLogUseCase.loadStockLog(pageable, uuid));
    }
}