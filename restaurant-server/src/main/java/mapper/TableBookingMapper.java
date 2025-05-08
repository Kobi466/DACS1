package mapper;

import dto.TableBookingDTO;
import model.TableBooking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TableBookingMapper extends BaseMapper<TableBooking, TableBookingDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa TableBooking và TableBookingDTO
}
