package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.MemberStockEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.MemberStockJpaRepository;
import ScreeningHumanity.TradeServer.application.port.out.dto.StockOutDto;
import ScreeningHumanity.TradeServer.application.port.out.outport.LoadMemberStockPort;
import ScreeningHumanity.TradeServer.application.port.out.outport.SaveMemberStockPort;
import ScreeningHumanity.TradeServer.domain.MemberStock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberStockAdaptor implements SaveMemberStockPort, LoadMemberStockPort {

    private final MemberStockJpaRepository memberStockJpaRepository;
    private final ModelMapper modelMapper;

    @Override
    public Optional<MemberStock> loadMemberStock(String uuid, String stockCode) {
        Optional<MemberStockEntity> loadData =
                memberStockJpaRepository.findAllByUuidAndStockCode(uuid, stockCode);

        return Optional.ofNullable(MemberStock
                .builder()
                .id(loadData.get().getId())
                .uuid(loadData.get().getUuid())
                .amount(loadData.get().getAmount())
                .totalPrice(loadData.get().getTotalPrice())
                .totalAmount(loadData.get().getTotalAmount())
                .stockCode(loadData.get().getStockCode())
                .stockName(loadData.get().getStockName())
                .build());
    }

    @Override
    public MemberStock saveMemberStock(MemberStock memberStock) {
        MemberStockEntity saveData = memberStockJpaRepository.save(
                MemberStockEntity.toEntityFrom(memberStock));

        return MemberStockEntity.toDomainFrom(saveData);
    }

    @Override
    public void DeleteMemberStock(MemberStock memberStock) {
        memberStockJpaRepository.deleteById(memberStock.getId());
    }
}
