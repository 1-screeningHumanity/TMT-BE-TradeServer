package ScreeningHumanity.TradeServer.docs.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;

import ScreeningHumanity.TradeServer.adaptor.in.web.controller.StockLogController;
import ScreeningHumanity.TradeServer.application.port.in.usecase.StockLogUseCase;
import ScreeningHumanity.TradeServer.application.port.out.dto.StockLogOutDto;
import ScreeningHumanity.TradeServer.docs.RestDocsSupport;
import ScreeningHumanity.TradeServer.domain.StockLogStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class StockLogControllerDocsTest extends RestDocsSupport {

    private final StockLogUseCase stockLogUseCase = Mockito.mock(StockLogUseCase.class);

    @Override
    protected Object initController(ModelMapper modelMapper) {
        return new StockLogController(stockLogUseCase, decodingToken);
    }

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
                .andDo(document("stock-list",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Bearer Access Token")
                        ),
                        queryParameters(
                                parameterWithName("page")
                                        .description("페이지 번호")
                                        .attributes(key("default").value("0"))
                                        .optional(),
                                parameterWithName("size")
                                        .description("페이지 크기")
                                        .attributes(key("default").value("50"))
                                        .optional()
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
                                        .description("로그 Index 순서"),
                                fieldWithPath("data[].time").type(JsonFieldType.STRING)
                                        .description("매매 시간"),
                                fieldWithPath("data[].stockName").type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("data[].price").type(JsonFieldType.STRING)
                                        .description("매매 단가"),
                                fieldWithPath("data[].amount").type(JsonFieldType.STRING)
                                        .description("매매 체결 수량"),
                                fieldWithPath("data[].totalPrice").type(JsonFieldType.STRING)
                                        .description("총 매매 체결 가격"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING)
                                        .description("매매 유형")
                        )
                ));
    }
}
