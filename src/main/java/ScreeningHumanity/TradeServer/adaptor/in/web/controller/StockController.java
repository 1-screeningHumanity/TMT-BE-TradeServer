package ScreeningHumanity.TradeServer.adaptor.in.web.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.application.port.in.dto.StockInDto;
import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import ScreeningHumanity.TradeServer.global.common.token.DecodingToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class StockController {

    private final StockUseCase stockUseCase;
    private final ModelMapper modelMapper;
    private final DecodingToken decodingToken;

    @PostMapping("/buy")
    public BaseResponse<Void> stockBuy(
            @Valid @RequestBody RequestDto.StockBuy requestStockBuyDto,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        stockUseCase.buyStock(
                modelMapper.map(requestStockBuyDto, StockInDto.Buy.class),
                decodingToken.getUuid(accessToken),
                accessToken);
        return new BaseResponse<>();
    }

    @PostMapping("/sale")
    public BaseResponse<Void> stockSale(
            @Valid @RequestBody RequestDto.StockSale requestStockSaleDto,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        stockUseCase.saleStock(
                modelMapper.map(requestStockSaleDto, StockInDto.Sale.class),
                decodingToken.getUuid(accessToken));
        return new BaseResponse<>();
    }
}