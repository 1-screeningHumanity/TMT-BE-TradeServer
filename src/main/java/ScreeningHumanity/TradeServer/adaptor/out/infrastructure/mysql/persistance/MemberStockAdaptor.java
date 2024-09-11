package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.MemberStockEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.MemberStockJpaRepository;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveMemberStockPort;
import ScreeningHumanity.TradeServer.domain.MemberStock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberStockAdaptor implements SaveMemberStockPort, LoadMemberStockPort {

    private final MemberStockJpaRepository memberStockJpaRepository;

    @Override
    public Optional<MemberStock> loadMemberStock(String uuid, String stockCode) {
        return memberStockJpaRepository.findAllByUuidAndStockCode(uuid, stockCode)
                        .map(entity -> MemberStock.builder()
                                .id(entity.getId())
                                .uuid(entity.getUuid())
                                .amount(entity.getAmount())
                                .totalPrice(entity.getTotalPrice())
                                .totalAmount(entity.getTotalAmount())
                                .stockCode(entity.getStockCode())
                                .stockName(entity.getStockName())
                                .build());
    }

    @Override
    public MemberStock saveMemberStock(MemberStock memberStock) {
        MemberStockEntity saveData = memberStockJpaRepository.save(toEntity(memberStock));
        return toDomain(saveData);
    }

    private MemberStockEntity toEntity(MemberStock memberStock) {
        return MemberStockEntity.builder()
                .id(memberStock.getId())
                .uuid(memberStock.getUuid())
                .amount(memberStock.getAmount())
                .totalPrice(memberStock.getTotalPrice())
                .totalAmount(memberStock.getTotalAmount())
                .stockCode(memberStock.getStockCode())
                .stockName(memberStock.getStockName())
                .build();
    }

    private MemberStock toDomain(MemberStockEntity entity) {
        return MemberStock.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .amount(entity.getAmount())
                .totalPrice(entity.getTotalPrice())
                .totalAmount(entity.getTotalAmount())
                .stockCode(entity.getStockCode())
                .stockName(entity.getStockName())
                .build();
    }
}
