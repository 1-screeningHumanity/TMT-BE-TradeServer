package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationBuyEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.ReservationSaleEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.ReservationBuyJpaRepository;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.ReservationSaleJpaRepository;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadReservationStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveReservationStockPort;
import ScreeningHumanity.TradeServer.domain.ReservationBuy;
import ScreeningHumanity.TradeServer.domain.ReservationSale;
import java.util.List;
import java.util.Optional;
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
    public void saveReservationBuyStock(ReservationBuy reservationBuy) {
        reservationBuyJpaRepository.save(
                ReservationBuyEntity
                        .builder()
                        .id(reservationBuy.getId())
                        .uuid(reservationBuy.getUuid())
                        .price(reservationBuy.getPrice())
                        .amount(reservationBuy.getAmount())
                        .stockCode(reservationBuy.getStockCode())
                        .stockName(reservationBuy.getStockName())
                        .build());
    }

    @Override
    public void saveReservationSaleStock(ReservationSale reservationSale) {
        reservationSaleJpaRepository.save(
                ReservationSaleEntity
                        .builder()
                        .id(reservationSale.getId())
                        .uuid(reservationSale.getUuid())
                        .price(reservationSale.getPrice())
                        .amount(reservationSale.getAmount())
                        .stockCode(reservationSale.getStockCode())
                        .stockName(reservationSale.getStockName())
                        .build());
    }

    @Override
    public Optional<ReservationSale> deleteReservationSaleStock(Long saleId) {
        return reservationSaleJpaRepository.findById(saleId)
                .map(findData -> {
                    reservationSaleJpaRepository.deleteById(saleId);
                    return ReservationSale.builder()
                            .id(findData.getId())
                            .uuid(findData.getUuid())
                            .price(findData.getPrice())
                            .amount(findData.getAmount())
                            .createdAt(findData.getCreatedAt())
                            .stockCode(findData.getStockCode())
                            .stockName(findData.getStockName())
                            .build();
                });
    }

    @Override
    public Optional<ReservationBuy> deleteReservationBuyStock(Long buyId) {

        return reservationBuyJpaRepository.findById(buyId)
                .map(findData -> {
                    reservationBuyJpaRepository.deleteById(buyId);
                    return ReservationBuy.builder()
                            .id(findData.getId())
                            .uuid(findData.getUuid())
                            .price(findData.getPrice())
                            .amount(findData.getAmount())
                            .createdAt(findData.getCreatedAt())
                            .stockCode(findData.getStockCode())
                            .stockName(findData.getStockName())
                            .build();
                });
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
    public List<ReservationBuy> findMatchBuyStock(String stockCode, Long nowPrice) {
        List<ReservationBuyEntity> findList = reservationBuyJpaRepository.findByStockCodeAndPrice(
                stockCode, nowPrice);
        return findList.stream()
                .map(entity -> modelMapper.map(entity, ReservationBuy.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationSale> findMatchSaleStock(String stockCode, Long nowPrice) {
        List<ReservationSaleEntity> findList = reservationSaleJpaRepository.findByStockCodeAndPrice(
                stockCode, nowPrice);
        return findList.stream()
                .map(entity -> modelMapper.map(entity, ReservationSale.class))
                .collect(Collectors.toList());
    }
}
