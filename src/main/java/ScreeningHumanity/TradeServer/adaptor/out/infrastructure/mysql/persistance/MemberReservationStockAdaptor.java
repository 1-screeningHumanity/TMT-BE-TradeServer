package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import ScreeningHumanity.TradeServer.adaptor.in.kafka.dto.RealChartInputDto;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationBuyEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationSaleEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.ReservationBuyJpaRepository;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.ReservationSaleJpaRepository;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadReservationStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveReservationStockPort;
import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import ScreeningHumanity.TradeServer.global.common.exception.CustomException;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberReservationStockAdaptor
        implements SaveReservationStockPort, LoadReservationStockPort {

    private final ReservationBuyJpaRepository reservationBuyJpaRepository;
    private final ReservationSaleJpaRepository reservationSaleJpaRepository;
    private final ModelMapper modelMapper;

    @Override
    public ReservationBuy SaveReservationBuyStock(ReservationBuy reservationBuy) {
        ReservationBuyEntity saveData = reservationBuyJpaRepository.save(
                ReservationBuyEntity.toEntityFrom(reservationBuy));

        return ReservationBuyEntity.toDomainFrom(saveData);
    }

    @Override
    public void SaveReservationSaleStock(ReservationSale reservationSale) {
        reservationSaleJpaRepository.save(ReservationSaleEntity.toEntityFrom(reservationSale));
    }

    @Override
    public ReservationSale DeleteReservationSaleStock(Long saleId) {
        ReservationSaleEntity findResult = reservationSaleJpaRepository.findById(saleId).
                orElseThrow(() -> new CustomException(
                        BaseResponseCode.DELETE_RESERVATION_SALE_STOCK_ERROR));
        reservationSaleJpaRepository.delete(findResult);

        return ReservationSaleEntity.toDomainFrom(findResult);
    }

    @Override
    public ReservationBuy DeleteReservationBuyStock(Long buyId) {
        ReservationBuyEntity findResult = reservationBuyJpaRepository.findById(buyId).
                orElseThrow(() -> new CustomException(
                        BaseResponseCode.DELETE_RESERVATION_BUY_STOCK_ERROR));
        reservationBuyJpaRepository.delete(findResult);

        return ReservationBuyEntity.toDomainFrom(findResult);
    }

    /**
     * 예약 구매의 매수 채결은 삭제 처리이다.
     * 실제 memberStock 으로의 이전과 StockLog의 등록은 다른 port의 메서드를 통해 진행한다.
     * @param buyList
     */
    @Override
    public void concludeBuyStock(List<ReservationBuy> buyList) {
        buyList.forEach(reservationBuy -> reservationBuyJpaRepository.deleteById(reservationBuy.getId()));
    }

    /**
     * 예약 판매의 매수 채결은 삭제 처리이다.
     * 실제 memberStock 으로의 이전과 StockLog의 등록은 다른 port의 메서드를 통해 진행한다.
     * @param saleList
     */
    @Override
    public void concludeSaleStock(List<ReservationSale> saleList) {
        saleList.forEach(reservationSale -> reservationSaleJpaRepository.deleteById(reservationSale.getId()));
    }

    @Override
    public List<ReservationBuy> loadReservationBuy(String uuid) {
        List<ReservationBuyEntity> findList = reservationBuyJpaRepository.findAllByUuid(uuid);
        return findList.stream()
                .map(entity -> modelMapper.map(entity, ReservationBuy.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationSale> loadReservationSale(String uuid) {
        List<ReservationSaleEntity> findList = reservationSaleJpaRepository.findAllByUuid(uuid);
        return findList.stream()
                .map(entity -> modelMapper.map(entity, ReservationSale.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationBuy> findMatchBuyStock(RealChartInputDto dto) {
        List<ReservationBuyEntity> findList = reservationBuyJpaRepository.findByStockCodeAndPrice(
                dto.getStockCode(), dto.getPrice());
        return findList.stream()
                .map(entity -> modelMapper.map(entity, ReservationBuy.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationSale> findMatchSaleStock(RealChartInputDto dto) {
        List<ReservationSaleEntity> findList = reservationSaleJpaRepository.findByStockCodeAndPrice(
                dto.getStockCode(), dto.getPrice());
        return findList.stream()
                .map(entity -> modelMapper.map(entity, ReservationSale.class))
                .collect(Collectors.toList());
    }
}
