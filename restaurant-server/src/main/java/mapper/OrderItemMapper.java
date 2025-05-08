package mapper;

import dto.OrderItemDTO;
import model.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper extends BaseMapper<OrderItem, OrderItemDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa OrderItem và OrderItemDTO
}
