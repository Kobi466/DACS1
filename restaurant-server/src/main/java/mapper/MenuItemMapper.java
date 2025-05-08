package mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface  MenuItemMapper extends BaseMapper<model.MenuItem, dto.MenuItemDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa MenuItem và MenuItemDTO
}
