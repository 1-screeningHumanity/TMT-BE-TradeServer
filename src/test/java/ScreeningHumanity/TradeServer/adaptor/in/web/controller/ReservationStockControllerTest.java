package ScreeningHumanity.TradeServer.adaptor.in.web.controller;

import static ScreeningHumanity.TradeServer.application.service.ReservationStockService.STATUS_SALE;
import static ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode.VALIDATION_FAIL_ERROR;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.IntegrationControllerTestSupporter;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationStockOutDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Slf4j
class ReservationStockControllerTest extends IntegrationControllerTestSupporter {

    @DisplayName("[Success] 예약 매수 접수 처리를 진행 합니다.")
    @Test
    void reservationStockBuy() throws Exception {
        // given
        RequestDto.StockReservationBuy request = RequestDto.StockReservationBuy
                .builder()
                .stockCode("005930")
                .price(1000L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("[Fail] 예약 매수 접수 시, 주식 코드는 필수입니다.")
    @Test
    void reservationStockBuyStockCodeCase1() throws Exception {
        // given
        RequestDto.StockReservationBuy request = RequestDto.StockReservationBuy
                .builder()
                .price(1000L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주식 코드는 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 예약 매수 접수 시, 매수 가격은 100원 이상부터 입니다.")
    @Test
    void reservationStockBuyPriceCase1() throws Exception {
        // given
        RequestDto.StockReservationBuy request = RequestDto.StockReservationBuy
                .builder()
                .stockCode("005930")
                .price(99L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.message").value("매수 가격은 100원 이상부터 입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 예약 매수 접수 시, 매수 최소 수량은 1개 부터 입니다.")
    @Test
    void reservationStockBuyAmountCase1() throws Exception {
        // given
        RequestDto.StockReservationBuy request = RequestDto.StockReservationBuy
                .builder()
                .stockCode("005930")
                .price(100L)
                .amount(0L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("예약 매수 최소 수량은 1개 이상부터 입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 예약 매수 접수 시, 주식 이름은 필수 입니다.")
    @Test
    void reservationStockBuyStockNameCase1() throws Exception {
        // given
        RequestDto.StockReservationBuy request = RequestDto.StockReservationBuy
                .builder()
                .stockCode("005930")
                .price(100L)
                .amount(1L)
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주식 이름은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    //--------------------------------------------------------------------

    @DisplayName("[Success] 예약 매도 접수 처리를 진행 합니다.")
    @Test
    void reservationStockSale() throws Exception {
        // given
        RequestDto.StockReservationSale request = RequestDto.StockReservationSale
                .builder()
                .stockCode("005930")
                .price(1000L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("[Fail] 예약 매도 접수 시, 주식 코드는 필수입니다.")
    @Test
    void reservationStockSaleStockCodeCase1() throws Exception {
        // given
        RequestDto.StockReservationSale request = RequestDto.StockReservationSale
                .builder()
                .price(1000L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주식 코드는 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 예약 매도 접수 시, 매수 가격은 100원 이상부터 입니다.")
    @Test
    void reservationStockSalePriceCase1() throws Exception {
        // given
        RequestDto.StockReservationBuy request = RequestDto.StockReservationBuy
                .builder()
                .stockCode("005930")
                .price(99L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.message").value("매도 가격은 100원 이상부터 입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 예약 매도 접수 시, 매수 최소 수량은 1개 부터 입니다.")
    @Test
    void reservationStockSaleAmountCase1() throws Exception {
        // given
        RequestDto.StockReservationSale request = RequestDto.StockReservationSale
                .builder()
                .stockCode("005930")
                .price(100L)
                .amount(0L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("예약 매도 최소 수량은 1개 이상부터 입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 예약 매매 접수 시, 주식 이름은 필수 입니다.")
    @Test
    void reservationStockSaleStockNameCase1() throws Exception {
        // given
        RequestDto.StockReservationSale request = RequestDto.StockReservationSale
                .builder()
                .stockCode("005930")
                .price(100L)
                .amount(1L)
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/reservation/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주식 이름은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    //--------------------------------------------------------------------

    @DisplayName("[Success] 예약 매매 리스트를 조회합니다.")
    @Test
    void reservationStockLog() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        String accessToken = "testToken";

        List<ReservationStockOutDto.Logs> result = List.of(
                ReservationStockOutDto.Logs
                        .builder()
                        .id(1L)
                        .createdAt(now)
                        .time(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .stockName("삼성전자")
                        .price("100")
                        .amount("1")
                        .totalPrice("100")
                        .status(STATUS_SALE)
                        .stockCode("005930")
                        .build()
        );

        // stubbing
        BDDMockito
                .given(reservationStockUseCase.buySaleLog(anyString()))
                .willReturn(result);
        BDDMockito
                .given(decodingToken.getUuid(anyString()))
                .willReturn(accessToken);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/reservation/trade-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("요청 응답 성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1L));

        BDDMockito.verify(reservationStockUseCase).buySaleLog(anyString());
        BDDMockito.verify(decodingToken).getUuid(anyString());
    }

    //--------------------------------------------------------------------

    @DisplayName("[Success] 예약 매수 취소")
    @Test
    void reservationDeleteSaleStock() throws Exception {
        // given
        Long id = 1L;

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/reservation/sale/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("요청 응답 성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
    }

    //--------------------------------------------------------------------

    @DisplayName("[Success] 예약 매도 취소")
    @Test
    void reservationDeleteBuyStock() throws Exception {
        // given
        Long id = 1L;

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/reservation/buy/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("요청 응답 성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
    }
}