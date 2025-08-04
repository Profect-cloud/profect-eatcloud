package profect.eatcloud.global.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import profect.eatcloud.domain.admin.entity.*;
import profect.eatcloud.domain.customer.entity.QCustomer;
import profect.eatcloud.domain.manager.entity.QManager;
import profect.eatcloud.domain.order.entity.QOrder;
import profect.eatcloud.domain.order.entity.QReview;
import profect.eatcloud.domain.payment.entity.QPayment;
import profect.eatcloud.domain.store.entity.QDailyMenuSales;
import profect.eatcloud.domain.store.entity.QDailyStoreSales;
import profect.eatcloud.domain.store.entity.QMenu;
import profect.eatcloud.domain.store.entity.QStore;


public class SoftDeletePredicates {

    public static BooleanExpression customerActive() {
        return QCustomer.customer.timeData.deletedAt.isNull();
    }

    public static BooleanExpression storeActive() {
        return QStore.store.timeData.deletedAt.isNull();
    }

    public static BooleanExpression menuActive() {
        return QMenu.menu.timeData.deletedAt.isNull();
    }

    public static BooleanExpression orderActive() {
        return QOrder.order.timeData.deletedAt.isNull();
    }

    public static BooleanExpression reviewActive() {
        return QReview.review.timeData.deletedAt.isNull();
    }

    public static BooleanExpression paymentActive() {
        return QPayment.payment.timeData.deletedAt.isNull();
    }

    public static BooleanExpression managerActive() {
        return QManager.manager.timeData.deletedAt.isNull();
    }

    public static BooleanExpression adminActive() {
        return QAdmin.admin.timeData.deletedAt.isNull();
    }

    public static BooleanExpression dailyStoreSalesActive() {
        return QDailyStoreSales.dailyStoreSales.timeData.deletedAt.isNull();
    }

    public static BooleanExpression dailyMenuSalesActive() {
        return QDailyMenuSales.dailyMenuSales.timeData.deletedAt.isNull();
    }

    public static BooleanExpression salesWithStoreActive() {
        return dailyStoreSalesActive().and(storeActive());
    }

    public static BooleanExpression menuSalesWithStoreAndMenuActive() {
        return dailyMenuSalesActive()
                .and(storeActive())
                .and(menuActive());
    }

    public static BooleanExpression orderWithCustomerActive() {
        return orderActive().and(customerActive());
    }

    public static BooleanExpression orderWithStoreActive() {
        return orderActive().and(storeActive());
    }

    public static BooleanExpression orderFullyActive() {
        return orderActive()
                .and(customerActive())
                .and(storeActive());
    }
}