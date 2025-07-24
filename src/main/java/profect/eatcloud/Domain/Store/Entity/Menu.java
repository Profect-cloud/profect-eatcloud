package profect.eatcloud.Domain.Store.Entity;

import jakarta.persistence.*;
import lombok.*;
import profect.eatcloud.Domain.Store.Dto.MenuRequestDto;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_menus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {

    @Id
    @GeneratedValue
    @Column(name = "menu_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "menu_num", nullable = false)
    private int menuNum;

    @Column(name = "menu_name", nullable = false, length = 200)
    private String menuName;

    @Column(name = "menu_category_code", nullable = false, length = 30)
    private String menuCategoryCode;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "p_time_id", nullable = false)
    private UUID pTimeId;


    public void updateFromDto(MenuRequestDto dto) {
        this.menuNum = dto.getMenuNum();
        this.menuName = dto.getMenuName();
        this.menuCategoryCode = dto.getMenuCategoryCode();
        this.price = dto.getPrice();
        this.description = dto.getDescription();
        this.isAvailable = dto.isAvailable();
        this.imageUrl = dto.getImageUrl();
        // store나 p_time_id 등은 업데이트 대상이 아니면 생략
    }

}
