package profect.eatcloud.domain.Store.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import profect.eatcloud.domain.Store.Entity.DailyStoreSales;
import profect.eatcloud.domain.Store.Entity.DailyStoreSalesId;
import profect.eatcloud.domain.Store.Entity.*;
import profect.eatcloud.Global.TimeData.BaseTimeRepository;
import profect.eatcloud.Global.QueryDSL.SoftDeletePredicates;
import profect.eatcloud.Global.QueryDSL.SpringContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DailyStoreSalesRepository extends BaseTimeRepository<DailyStoreSales, DailyStoreSalesId> {

    default List<DailyStoreSales> findByStoreIdAndDateRangeActive(UUID storeId, LocalDate startDate, LocalDate endDate) {
        JPAQueryFactory queryFactory = getQueryFactory();
        QDailyStoreSales sales = QDailyStoreSales.dailyStoreSales;
        QStore store = QStore.store;

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(SoftDeletePredicates.salesWithStoreActive());
        condition.and(store.storeId.eq(storeId));

        if (startDate != null) {
            condition.and(sales.saleDate.goe(startDate));
        }
        if (endDate != null) {
            condition.and(sales.saleDate.loe(endDate));
        }

        return queryFactory
                .selectFrom(sales)
                .join(sales.store, store)
                .where(condition)
                .orderBy(sales.saleDate.asc())
                .fetch();
    }
    default JPAQueryFactory getQueryFactory() {
        return SpringContext.getBean(JPAQueryFactory.class);
    }
}