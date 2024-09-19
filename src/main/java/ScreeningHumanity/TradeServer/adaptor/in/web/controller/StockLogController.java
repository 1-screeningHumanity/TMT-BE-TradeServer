package ScreeningHumanity.TradeServer.adaptor.in.web.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.application.port.in.usecase.StockLogUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.StockLogOutDto;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import ScreeningHumanity.TradeServer.global.common.token.DecodingToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Stock Buy/Sale Logging API", description = "주식 매매 로그 확인 API")
public class StockLogController {

    private final StockLogUseCase stockLogUseCase;
    private final DecodingToken decodingToken;

    @Operation(summary = "매수/매도 조회 API", description = "매수/매도 조회 API 호출")
    @GetMapping("/trade-lists")
    public BaseResponse<List<StockLogOutDto>> stockLog(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "50", required = false) int size,
//            @RequestParam(defaultValue = "createdAt", required = false) String sortField,
//            @RequestParam(defaultValue = "DESC", required = false) String sortDirection,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        log.info("매수/매도 조회 API 실행");
        Pageable pageable = PageRequest.of(page, size);
//                PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortField));
        String uuid = decodingToken.getUuid(accessToken);

        return new BaseResponse<>(stockLogUseCase.loadStockLog(pageable, uuid));
    }
}
