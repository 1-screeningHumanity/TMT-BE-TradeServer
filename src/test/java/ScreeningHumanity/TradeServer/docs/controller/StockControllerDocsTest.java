package ScreeningHumanity.TradeServer.docs.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import ScreeningHumanity.TradeServer.adaptor.in.web.controller.StockController;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto.StockBuy;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto.StockSale;
import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class StockControllerDocsTest extends RestDocsSupport {

    private final StockUseCase stockUseCase = Mockito.mock(StockUseCase.class);

    @Override
    protected Object initController(ModelMapper modelMapper) {
        return new StockController(stockUseCase, modelMapper, decodingToken);
    }

    @DisplayName("[Docs] 즉시 매수 주문 처리 API")
    @Test
    void stockBuy() throws Exception {
        RequestDto.StockBuy request = StockBuy
                .builder()
                .stockCode("005930")
                .price(10000L)
                .amount(1L)
                .stockName("삼성전자")
                .build();
        String accessToken = "testToken";
        mockMvc.perform(MockMvcRequestBuilders.post("/buy")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("stock-buy",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Bearer Access Token")
                        ),
                        requestFields(
                                fieldWithPath("stockCode").type(JsonFieldType.STRING)
                                        .description("주식 코드"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                        .description("매수 가격"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER)
                                        .description("매수 수량"),
                                fieldWithPath("stockName").type(JsonFieldType.STRING)
                                        .description("주식 이름")
                        ),
                        responseFields(
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("응답 상태"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                        .description("성공 유무"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메세지"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("응답 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("응답 데이터")
                        )
                        ));
    }

    @DisplayName("[Docs] 즉시 매도 주문을 처리합니다.")
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("stock-sale",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Bearer Access Token")
                        ),
                        requestFields(
                                fieldWithPath("stockCode").type(JsonFieldType.STRING)
                                        .description("주식 코드"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                        .description("매도 가격"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER)
                                        .description("매도 수량"),
                                fieldWithPath("stockName").type(JsonFieldType.STRING)
                                        .description("주식 이름")
                        ),
                        responseFields(
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("응답 상태"),
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                        .description("성공 유무"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메세지"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("응답 코드"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("응답 데이터")
                        )
                ));
    }
}