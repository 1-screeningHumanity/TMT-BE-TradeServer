package ScreeningHumanity.TradeServer.docs.controller;

import static ScreeningHumanity.TradeServer.application.service.ReservationStockService.STATUS_SALE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import ScreeningHumanity.TradeServer.adaptor.in.web.controller.ReservationStockController;
import ScreeningHumanity.TradeServer.application.port.in.dto.RequestDto;
import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationStockOutDto;
import ScreeningHumanity.TradeServer.application.port.out.dto.ReservationStockOutDto.Logs;
import ScreeningHumanity.TradeServer.docs.RestDocsSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ReservationStockControllerDocsTest extends RestDocsSupport {

    private final ReservationStockUseCase reservationStockUseCase = Mockito.mock(ReservationStockUseCase.class);

    @Override
    protected Object initController(ModelMapper modelMapper) {
        return new ReservationStockController(modelMapper, decodingToken, reservationStockUseCase);
    }

    @DisplayName("[Docs] 예약 매수 접수 처리를 진행 합니다.")
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("reservation-stock-buy",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Bearer Access Token")
                        ),
                        requestFields( //요청에 필요한 내용들 명시. stockBuy는 RequestDto.StockBuy, token이 필요
                                fieldWithPath("stockCode").type(JsonFieldType.STRING)
                                        .description("주식 코드"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                        .description("예약 매수 가격"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER)
                                        .description("예약 매수 수량"),
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

    @DisplayName("[Docs] 예약 매도 접수 처리를 진행 합니다.")
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("reservation-stock-sale",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Bearer Access Token")
                        ),
                        requestFields(
                                fieldWithPath("stockCode").type(JsonFieldType.STRING)
                                        .description("주식 코드"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                        .description("예약 매도 가격"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER)
                                        .description("예약 매도 수량"),
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

    @DisplayName("[Docs] 예약 매매 리스트를 조회합니다.")
    @Test
    void reservationStockLog() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        String accessToken = "testToken";

        List<Logs> result = List.of(
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
                .andDo(document("reservation-stock-log",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Bearer Access Token")
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
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("응답 데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("매매 id"),
                                fieldWithPath("data[].time").type(JsonFieldType.STRING)
                                        .description("생성 년월일"),
                                fieldWithPath("data[].stockName").type(JsonFieldType.STRING)
                                        .description("주식 이름"),
                                fieldWithPath("data[].price").type(JsonFieldType.STRING)
                                        .description("예약 매매 가격"),
                                fieldWithPath("data[].amount").type(JsonFieldType.STRING)
                                        .description("예약 매매 수량"),
                                fieldWithPath("data[].totalPrice").type(JsonFieldType.STRING)
                                        .description("예약 매매 총 가격"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING)
                                        .description("예약 매매 종류"),
                                fieldWithPath("data[].stockCode").type(JsonFieldType.STRING)
                                        .description("주식 번호")
                        )
                ));
    }

    @DisplayName("[Docs] 예약 매수 취소")
    @Test
    void reservationDeleteSaleStock() throws Exception {
        // given
        Long id = 1L;

        // when // then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/reservation/sale/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("reservation-stock-sale-delete",
                        pathParameters(
                                parameterWithName("id").description("취소할 예약 매도의 ID")),
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

    @DisplayName("[Docs] 예약 매도 취소")
    @Test
    void reservationDeleteBuyStock() throws Exception {
        // given
        Long id = 1L;

        // when // then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/reservation/buy/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("reservation-stock-buy-delete",
                        pathParameters(
                                parameterWithName("id").description("취소할 예약 매수의 ID")),
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
