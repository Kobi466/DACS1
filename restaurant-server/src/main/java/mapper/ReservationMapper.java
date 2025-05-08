package mapper;

import dto.ReservationDTO;
import model.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends BaseMapper<Reservation, ReservationDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa Reservation và ReservationDTO
}
