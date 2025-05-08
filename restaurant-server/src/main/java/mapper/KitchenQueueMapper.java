package mapper;

import dto.KitchenQueueDTO;
import model.KitchenQueue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KitchenQueueMapper extends BaseMapper<KitchenQueue, KitchenQueueDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa KitchenQueue và KitchenQueueDTO
}
