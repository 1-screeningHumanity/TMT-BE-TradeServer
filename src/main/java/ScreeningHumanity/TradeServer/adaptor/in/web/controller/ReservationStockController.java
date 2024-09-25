package ScreeningHumanity.TradeServer.adaptor.in.web.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.application.port.in.dto.ReservationStockInDto;
import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationStockOutDto;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import ScreeningHumanity.TradeServer.global.common.token.DecodingToken;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
@Slf4j
public class ReservationStockController {

    private final ModelMapper modelMapper;
    private final DecodingToken decodingToken;
    private final ReservationStockUseCase reservationStockUseCase;

    @PostMapping("/buy")
    public BaseResponse<Void> reservationStockBuy(
            @Valid @RequestBody RequestDto.StockReservationBuy requestDto,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        reservationStockUseCase.buyStock(
                modelMapper.map(requestDto, ReservationStockInDto.Buy.class),
                decodingToken.getUuid(accessToken),
                accessToken
        );
        return new BaseResponse<>();
    }

    @PostMapping("/sale")
    public BaseResponse<Void> reservationStockSale(
            @Valid @RequestBody RequestDto.StockReservationSale requestDto,
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        reservationStockUseCase.saleStock(
                modelMapper.map(requestDto, ReservationStockInDto.Sale.class),
                decodingToken.getUuid(accessToken));
        return new BaseResponse<>();
    }

    @GetMapping("/trade-lists")
    public BaseResponse<List<ReservationStockOutDto.Logs>> reservationStockLog(
            @RequestHeader(AUTHORIZATION) String accessToken
    ) {
        List<ReservationStockOutDto.Logs> result = reservationStockUseCase.buySaleLog(
                decodingToken.getUuid(accessToken));
        return new BaseResponse<>(result);
    }

    @DeleteMapping("/sale/{id}")
    public BaseResponse<Void> reservationDeleteSaleStock(
            @PathVariable("id") Long id
    ) {
        reservationStockUseCase.cancelReservationSaleStock(id, true);
        return new BaseResponse<>();
    }

    @DeleteMapping("/buy/{id}")
    public BaseResponse<Void> reservationDeleteBuyStock(
            @PathVariable("id") Long id
    ) {
        reservationStockUseCase.cancelReservationBuyStock(id, true);
        return new BaseResponse<>();
    }
}
