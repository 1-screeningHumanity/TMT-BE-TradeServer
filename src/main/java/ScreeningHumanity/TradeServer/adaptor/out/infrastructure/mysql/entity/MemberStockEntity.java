package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity;

import ScreeningHumanity.TradeServer.domain.MemberStock;
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
@Table(name = "member_stock")
public class MemberStockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "stock_code", nullable = false)
    private Long stockCode;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    public static MemberStockEntity toEntityFrom(MemberStock memberStock){
        return MemberStockEntity
                .builder()
                .id(memberStock.getId())
                .uuid(memberStock.getUuid())
                .amount(memberStock.getAmount())
                .totalPrice(memberStock.getTotalPrice())
                .totalAmount(memberStock.getTotalAmount())
                .stockCode(memberStock.getStockCode())
                .stockName(memberStock.getStockName())
                .build();
    }
}