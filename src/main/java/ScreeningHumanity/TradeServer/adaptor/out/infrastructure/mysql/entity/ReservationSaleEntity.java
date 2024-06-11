package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity;

import ScreeningHumanity.TradeServer.domain.ReservationSale;
import ScreeningHumanity.TradeServer.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservation_sale")
public class ReservationSaleEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    public static ReservationSaleEntity toEntityFrom(ReservationSale reservationSale){
        return ReservationSaleEntity
                .builder()
                .id(reservationSale.getId())
                .uuid(reservationSale.getUuid())
                .amount(reservationSale.getAmount())
                .price(reservationSale.getPrice())
                .stockCode(reservationSale.getStockCode())
                .stockName(reservationSale.getStockName())
                .build();
    }

    public static ReservationSale toDomainFrom(ReservationSaleEntity reservationSaleEntity){
        return ReservationSale
                .builder()
                .id(reservationSaleEntity.getId())
                .uuid(reservationSaleEntity.getUuid())
                .amount(reservationSaleEntity.getAmount())
                .price(reservationSaleEntity.getPrice())
                .stockCode(reservationSaleEntity.getStockCode())
                .stockName(reservationSaleEntity.getStockName())
                .build();
    }
}
