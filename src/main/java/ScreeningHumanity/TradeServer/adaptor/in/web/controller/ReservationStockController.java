package ScreeningHumanity.TradeServer.adaptor.in.web.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.adaptor.in.web.vo.RequestVo;
import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationLogOutDto;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import ScreeningHumanity.TradeServer.global.common.token.DecodingToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
@Tag(name = "Reservation Stock Buy/Sale API", description = "주식 예약 매매 API")
public class ReservationStockController {

    private final ModelMapper modelMapper;
    private final DecodingToken decodingToken;
    private final ReservationStockUseCase reservationStockUseCase;

    @Operation(summary = "예약 매수 api", description = "예약 매수 API 호출")
    @PostMapping("/reservation/buy")
    public BaseResponse<Void> ReservationStockBuy(
            @RequestBody RequestVo.StockBuy requestStockBuyVo,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        reservationStockUseCase.BuyStock(
                modelMapper.map(requestStockBuyVo, ReservationStockUseCase.StockBuySaleDto.class),
                decodingToken.getUuid(accessToken));
        return new BaseResponse<>();
    }

    @Operation(summary = "예약 매도 api", description = "예약 매도 API 호출")
    @PostMapping("/reservation/sale")
    public BaseResponse<Void> ReservationStockSale(
            @RequestBody RequestVo.StockSale requestStockSaleVo,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        reservationStockUseCase.SaleStock(
                modelMapper.map(requestStockSaleVo, ReservationStockUseCase.StockBuySaleDto.class),
                decodingToken.getUuid(accessToken));
        return new BaseResponse<>();
    }

    @Operation(summary = "예약 매도/매수 조회 api", description = "예약 매도/매수 조회 API 호출")
    @PostMapping("/reservation/trade-lists")
    public BaseResponse<List<ReservationLogOutDto>> ReservationStockLog(
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        List<ReservationLogOutDto> result = reservationStockUseCase.BuySaleLog(
                decodingToken.getUuid(accessToken));
        return new BaseResponse<>(result);
    }
}
