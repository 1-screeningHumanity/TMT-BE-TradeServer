package ScreeningHumanity.TradeServer;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance.MemberReservationStockAdaptor;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance.MemberStockAdaptor;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance.StockLogAdaptor;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.MemberStockJpaRepository;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.ReservationBuyJpaRepository;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.ReservationSaleJpaRepository;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.StockLogJpaRepository;
import ScreeningHumanity.TradeServer.application.port.in.usecase.PaymentUseCase;
import ScreeningHumanity.TradeServer.application.port.out.outport.MessageQueuePort;
import ScreeningHumanity.TradeServer.application.service.ReservationStockService;
import ScreeningHumanity.TradeServer.application.service.StockBuySaleService;
import ScreeningHumanity.TradeServer.application.service.StockLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationSpringBootTestSupporter {

    @Autowired
    protected MemberReservationStockAdaptor memberReservationStockAdaptor;

    @Autowired
    protected MemberStockAdaptor memberStockAdaptor;

    @Autowired
    protected StockLogAdaptor stockLogAdaptor;

    @Autowired
    protected ReservationBuyJpaRepository reservationBuyJpaRepository;

    @Autowired
    protected ReservationSaleJpaRepository reservationSaleJpaRepository;

    @Autowired
    protected MemberStockJpaRepository memberStockJpaRepository;

    @Autowired
    protected StockLogJpaRepository stockLogJpaRepository;

    @Autowired
    protected ReservationStockService reservationStockService;

    @Autowired
    protected StockBuySaleService stockBuySaleService;

    @Autowired
    protected StockLogService stockLogService;

    //---

    @MockBean
    protected PaymentUseCase paymentUseCase;

    @MockBean
    protected MessageQueuePort messageQueuePort;
}
