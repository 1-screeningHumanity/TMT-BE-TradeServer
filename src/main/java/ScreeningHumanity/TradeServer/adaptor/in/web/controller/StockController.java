package ScreeningHumanity.TradeServer.adaptor.in.web.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.adaptor.in.web.vo.RequestVo;
import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import ScreeningHumanity.TradeServer.global.common.token.DecodingToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Stock Buy/Sale API", description = "주식 매수 API")
public class StockController {

    private final StockUseCase stockUseCase;
    private final ModelMapper modelMapper;
    private final DecodingToken decodingToken;

    @Operation(summary = "매수 api", description = "매수 API 호출")
    @PostMapping("/buy")
    public BaseResponse<Void> stockBuy(
            @RequestBody RequestVo.StockBuy requestStockBuyVo,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        stockUseCase.BuyStock(
                modelMapper.map(requestStockBuyVo, StockUseCase.StockBuySaleDto.class),
                decodingToken.getUuid(accessToken));
        return new BaseResponse<>();
    }

    @Operation(summary = "매도 api", description = "매도 API 호출")
    @PostMapping("/sale")
    public BaseResponse<Void> stockSale(
            @RequestBody RequestVo.StockSale requestStockSaleVo,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        stockUseCase.SaleStock(
                modelMapper.map(requestStockSaleVo, StockUseCase.StockBuySaleDto.class),
                decodingToken.getUuid(accessToken));
        return new BaseResponse<>();
    }
}