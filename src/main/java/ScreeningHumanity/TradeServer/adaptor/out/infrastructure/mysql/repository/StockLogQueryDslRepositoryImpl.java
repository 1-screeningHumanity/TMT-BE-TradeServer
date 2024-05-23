package ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.repository;

import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.QStockLogEntity;
import ScreeningHumanity.TradeServer.adaptor.out.infrastructure.mysql.entity.StockLogEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class StockLogQueryDslRepositoryImpl implements StockLogQueryDslRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StockLogEntity> findStockLogByPageable(Pageable pageable, String uuid) {
        QStockLogEntity stockLogEntity = QStockLogEntity.stockLogEntity;

        return jpaQueryFactory
                .select(stockLogEntity)
                .from(stockLogEntity)
                .where(stockLogEntity.uuid.eq(uuid))
                .orderBy(stockLogEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
