package mapper;

import dto.VoucherDTO;
import model.Voucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoucherMapper extends BaseMapper<Voucher, VoucherDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa Voucher và VoucherDTO
}
