package mapper;

import dto.RatingDTO;
import model.Rating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper extends BaseMapper<Rating, RatingDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa Rating và RatingDTO
}
