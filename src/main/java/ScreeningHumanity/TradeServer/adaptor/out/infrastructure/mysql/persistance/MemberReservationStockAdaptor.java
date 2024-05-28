package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

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
    public void SaveReservationBuyStock(ReservationBuy reservationBuy) {
        reservationBuyJpaRepository.save(ReservationBuyEntity.toEntityFrom(reservationBuy));
    }

    @Override
    public void SaveReservationSaleStock(ReservationSale reservationSale) {
        reservationSaleJpaRepository.save(ReservationSaleEntity.toEntityFrom(reservationSale));
    }

    @Override
    public void DeleteReservationSaleStock(Long saleId) {
        ReservationSaleEntity findResult = reservationSaleJpaRepository.findById(saleId).
                orElseThrow(() -> new CustomException(
                        BaseResponseCode.DELETE_RESERVATION_SALE_STOCK_ERROR));
        reservationSaleJpaRepository.delete(findResult);
    }

    @Override
    public void DeleteReservationBuyStock(Long saleId) {
        ReservationBuyEntity findResult = reservationBuyJpaRepository.findById(saleId).
                orElseThrow(() -> new CustomException(
                        BaseResponseCode.DELETE_RESERVATION_BUY_STOCK_ERROR));
        reservationBuyJpaRepository.delete(findResult);
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
}
