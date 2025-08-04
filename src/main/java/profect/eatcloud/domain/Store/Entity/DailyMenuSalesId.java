package profect.eatcloud.domain.Store.Entity;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class DailyMenuSalesId implements Serializable {
    private LocalDate saleDate;
    private UUID storeId;
    private UUID menuId;
}