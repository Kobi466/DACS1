package mapper;

import dto.CustomerDTO;
import model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {
    @Mapping(source = "customer_Id", target = "customerId") // Thêm cách ánh xạ từ customer_Id -> customerId
    CustomerDTO toDTO(Customer customer);
}