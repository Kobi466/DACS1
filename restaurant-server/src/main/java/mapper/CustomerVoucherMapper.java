package mapper;

import dto.CustomerVoucherDTO;
import model.CustomerVoucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerVoucherMapper extends BaseMapper<CustomerVoucher, CustomerVoucherDTO> {
    // MapStruct tự động tạo các phương thức chuyển đổi giữa CustomerVoucher và CustomerVoucherDTO
}
