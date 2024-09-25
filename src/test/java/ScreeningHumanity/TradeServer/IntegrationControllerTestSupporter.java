package ScreeningHumanity.TradeServer;

import ScreeningHumanity.TradeServer.adaptor.in.web.controller.ReservationStockController;
import ScreeningHumanity.TradeServer.adaptor.in.web.controller.StockController;
import ScreeningHumanity.TradeServer.adaptor.in.web.controller.StockLogController;
import ScreeningHumanity.TradeServer.application.port.in.usecase.ReservationStockUseCase;
import ScreeningHumanity.TradeServer.application.port.in.usecase.StockLogUseCase;
import ScreeningHumanity.TradeServer.application.port.in.usecase.StockUseCase;
import ScreeningHumanity.TradeServer.global.common.token.DecodingToken;
import ScreeningHumanity.TradeServer.global.config.ModelMapperConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        StockController.class,
        ReservationStockController.class,
        StockLogController.class
})
@Import({ModelMapperConfig.class})
@ActiveProfiles("test")
public abstract class IntegrationControllerTestSupporter {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    @MockBean
    protected DecodingToken decodingToken;

    @MockBean
    protected ReservationStockUseCase reservationStockUseCase;

    @MockBean
    protected StockLogUseCase stockLogUseCase;

    @MockBean
    protected StockUseCase stockUseCase;
}
