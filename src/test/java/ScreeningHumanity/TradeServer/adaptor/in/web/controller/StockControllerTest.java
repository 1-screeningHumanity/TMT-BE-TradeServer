package ScreeningHumanity.TradeServer.adaptor.in.web.controller;

import static ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode.VALIDATION_FAIL_ERROR;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.IntegrationControllerTestSupporter;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto.StockBuy;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto.StockSale;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class StockControllerTest extends IntegrationControllerTestSupporter {

    @DisplayName("[Success] 즉시 매수 주문을 처리합니다.")
    @Test
    void stockBuy() throws Exception {
        // given
        RequestDto.StockBuy request = StockBuy
                .builder()
                .stockCode("005930")
                .price(10000L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("[Fail] 즉시 매수 주문시, 주식 코드는 필수 입니다.")
    @Test
    void stockBuyStockCodeFailCase1() throws Exception {
        // given
        RequestDto.StockBuy request = StockBuy
                .builder()
                .price(10000L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주식 코드는 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 즉시 매수 주문시, 상품 가격은 필수 입니다.")
    @Test
    @Disabled
    void stockBuyPriceFailCase1() throws Exception {
        // given
        RequestDto.StockBuy request = StockBuy
                .builder()
                .amount(1L)
                .stockCode("005930")
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 가격은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 즉시 매수 주문시, 상품 가격은 최소 100원 이상입니다.")
    @Test
    void stockBuyPriceFailCase2() throws Exception {
        // given
        RequestDto.StockBuy request = StockBuy
                .builder()
                .amount(1L)
                .price(99L)
                .stockCode("005930")
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("매수 가격은 100원 이상부터 입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 즉시 매수 주문시, 매수 수량은 필수 입니다.")
    @Test
    @Disabled
    void stockBuyAmountFailCase1() throws Exception {
        // given
        RequestDto.StockBuy request = StockBuy
                .builder()
                .price(99L)
                .stockCode("005930")
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("매수 수량은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 즉시 매수 주문시, 매수 수량은 1개 이상 부터 입니다.")
    @Test
    void stockBuyAmountFailCase2() throws Exception {
        // given
        RequestDto.StockBuy request = StockBuy
                .builder()
                .price(1000L)
                .amount(0L)
                .stockCode("005930")
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("매수 최소 수량은 1개 이상부터 입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 즉시 매수 주문시, 주식 이름은 필수입니다.")
    @Test
    void stockBuyStockNameFailCase1() throws Exception {
        // given
        RequestDto.StockBuy request = StockBuy
                .builder()
                .price(1000L)
                .amount(1L)
                .stockCode("005930")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주식 이름은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Success] 즉시 매도 주문을 처리합니다.")
    @Test
    void stockSale() throws Exception {
        // given
        RequestDto.StockSale request = StockSale
                .builder()
                .stockCode("005930")
                .price(10000L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("[Fail] 즉시 매도 주문시, 주식 코드는 필수 입니다.")
    @Test
    void stockSaleStockCodeFailCase1() throws Exception {
        // given
        RequestDto.StockSale request = StockSale
                .builder()
                .price(10000L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주식 코드는 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 즉시 매도 주문시, 매도 가격은 100원 이상입니다.")
    @Test
    void stockSalePriceFailCase1() throws Exception {
        // given
        RequestDto.StockSale request = StockSale
                .builder()
                .stockCode("005930")
                .price(99L)
                .amount(1L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("매도 가격은 100원 이상부터 입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 즉시 매도 주문시, 매도 최소 수량은 1개 부터 입니다.")
    @Test
    void stockSaleAmountFailCase1() throws Exception {
        // given
        RequestDto.StockSale request = StockSale
                .builder()
                .stockCode("005930")
                .price(100L)
                .amount(0L)
                .stockName("삼성전자")
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("매도 최소 수량은 1개 이상부터 입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @DisplayName("[Fail] 즉시 매도 주문시, 매도 최소 수량은 ")
    @Test
    void stockSaleStockNameFailCase1() throws Exception {
        // given
        RequestDto.StockSale request = StockSale
                .builder()
                .stockCode("005930")
                .price(100L)
                .amount(1L)
                .build();

        String accessToken = "testToken";

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/sale")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주식 이름은 필수입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(VALIDATION_FAIL_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }
}