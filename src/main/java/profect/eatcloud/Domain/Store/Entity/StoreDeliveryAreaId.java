package profect.eatcloud.Domain.Store.Entity;

import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Data
public class StoreDeliveryAreaId implements Serializable {
    private UUID storeId;
    private UUID areaId;
}