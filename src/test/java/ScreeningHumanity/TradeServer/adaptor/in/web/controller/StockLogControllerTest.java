package ScreeningHumanity.TradeServer.adaptor.in.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import ScreeningHumanity.TradeServer.IntegrationControllerTestSupporter;
import ScreeningHumanity.TradeServer.application.port.out.dto.StockLogOutDto;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class StockLogControllerTest extends IntegrationControllerTestSupporter {

    @DisplayName("[Success] 매매 정보 조회를 처리합니다.")
    @Test
    void stockLog() throws Exception {
        // given
        int page = 0;
        int size = 50;
        String accessToken = "testToken";
        LocalDateTime now = LocalDateTime.now();

        StockLogOutDto data = StockLogOutDto
                .builder()
                .indexId(1L)
                .time(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .stockName("삼성전자")
                .price("1000")
                .amount("1")
                .totalPrice("1000")
                .status(StockLogStatus.BUY)
                .build();

        List<StockLogOutDto> result = List.of(data);

        // stubbing
        BDDMockito.when(stockLogUseCase.loadStockLog(any(), anyString()))
                .thenReturn(result);
        BDDMockito.when(decodingToken.getUuid(anyString()))
                .thenReturn(accessToken);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/trade-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", Integer.toString(page))
                        .param("size", Integer.toString(size))
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("요청 응답 성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1L));

        BDDMockito.verify(stockLogUseCase).loadStockLog(any(), anyString());
        BDDMockito.verify(decodingToken).getUuid(anyString());
    }

    @DisplayName("[Success] 매매 정보를 조회할때, page 와 size 의 요청이 없으면, 기본으로 첫페이지의 50개 매매 정보 조회를 처리합니다.")
    @Test
    void stockLogDefaultValue() throws Exception {
        // given
        String accessToken = "testToken";
        LocalDateTime now = LocalDateTime.now();

        StockLogOutDto data = StockLogOutDto
                .builder()
                .indexId(1L)
                .time(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .stockName("삼성전자")
                .price("1000")
                .amount("1")
                .totalPrice("1000")
                .status(StockLogStatus.BUY)
                .build();

        List<StockLogOutDto> result = List.of(data);

        // stubbing
        BDDMockito.when(stockLogUseCase.loadStockLog(any(), anyString()))
                .thenReturn(result);
        BDDMockito.when(decodingToken.getUuid(anyString()))
                .thenReturn(accessToken);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/trade-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("요청 응답 성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1L));

        BDDMockito.verify(stockLogUseCase).loadStockLog(any(), anyString());
        BDDMockito.verify(decodingToken).getUuid(anyString());
    }

    @DisplayName("[Fail] 매매 정보를 조회할때, page 정보에 음수가 들어오면 실패합니다.")
    @Test
    void stockLogNonPositivePageCase() throws Exception {
        // given
        String accessToken = "testToken";
        LocalDateTime now = LocalDateTime.now();
        int page = -1;
        int size = 50;

        StockLogOutDto data = StockLogOutDto
                .builder()
                .indexId(1L)
                .time(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .stockName("삼성전자")
                .price("1000")
                .amount("1")
                .totalPrice("1000")
                .status(StockLogStatus.BUY)
                .build();

        List<StockLogOutDto> result = List.of(data);

        // stubbing
        BDDMockito.when(stockLogUseCase.loadStockLog(any(), anyString()))
                .thenReturn(result);
        BDDMockito.when(decodingToken.getUuid(anyString()))
                .thenReturn(accessToken);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/trade-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", Integer.toString(size))
                        .param("page", Integer.toString(page))
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(BaseResponseCode.REQUEST_PARAM_ERROR.isSuccess()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(BaseResponseCode.REQUEST_PARAM_ERROR.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BaseResponseCode.REQUEST_PARAM_ERROR.getCode()));

        BDDMockito.verifyNoInteractions(stockLogUseCase);
        BDDMockito.verifyNoInteractions(decodingToken);
    }

    @DisplayName("[Fail] 매매 정보를 조회할때, size 정보에 음수가 들어오면 실패합니다.")
    @Test
    void stockLogNonPositiveSizeCase() throws Exception {
        // given
        String accessToken = "testToken";
        LocalDateTime now = LocalDateTime.now();
        int page = 0;
        int size = -1;

        StockLogOutDto data = StockLogOutDto
                .builder()
                .indexId(1L)
                .time(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .stockName("삼성전자")
                .price("1000")
                .amount("1")
                .totalPrice("1000")
                .status(StockLogStatus.BUY)
                .build();

        List<StockLogOutDto> result = List.of(data);

        // stubbing
        BDDMockito.when(stockLogUseCase.loadStockLog(any(), anyString()))
                .thenReturn(result);
        BDDMockito.when(decodingToken.getUuid(anyString()))
                .thenReturn(accessToken);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/trade-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", Integer.toString(size))
                        .param("page", Integer.toString(page))
                        .header(AUTHORIZATION, accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(BaseResponseCode.REQUEST_PARAM_ERROR.isSuccess()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(BaseResponseCode.REQUEST_PARAM_ERROR.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(BaseResponseCode.REQUEST_PARAM_ERROR.getCode()));

        BDDMockito.verifyNoInteractions(stockLogUseCase);
        BDDMockito.verifyNoInteractions(decodingToken);
    }
}