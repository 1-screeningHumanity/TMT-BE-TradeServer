package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.persistance;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.MemberStockEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository.MemberStockJpaRepository;
import ScreeningHumanity.TradeServer.application.port.out.dto.MemberStockOutDto;
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
    public Optional<MemberStockOutDto> LoadMemberStockByUuidAndStockCode(String uuid, String stockCode) {
        Optional<MemberStockEntity> loadMemberStock = memberStockJpaRepository.findAllByUuidAndStockCode(
                uuid, stockCode);
        return Optional.ofNullable(modelMapper.map(loadMemberStock, MemberStockOutDto.class));
    }

    @Override
    public void SaveMemberStock(MemberStock memberStock) {
        memberStockJpaRepository.save(MemberStockEntity.toEntityFrom(memberStock));
    }
}
