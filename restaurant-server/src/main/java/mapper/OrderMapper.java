package mapper;

import dto.OrderDTO;
import model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper extends BaseMapper<Order, OrderDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa Order và OrderDTO
}
