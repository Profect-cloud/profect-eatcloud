package profect.eatcloud.domain.Store.Entity;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class DailyStoreSalesId implements Serializable {
    private LocalDate saleDate;
    private UUID storeId;
}