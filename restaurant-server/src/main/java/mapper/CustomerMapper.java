package mapper;

import dto.CustomerDTO;
import model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {
    @Mapping(source = "customer_Id", target = "customerId") // Thêm cách ánh xạ từ customer_Id -> customerId
    CustomerDTO toDTO(Customer customer);
    static Customer toEntity(CustomerDTO customerDTO){
        Customer customer = new Customer();
//        customer.setCustomer_Id(customerDTO.getCustomerId());
        customer.setUserName(customerDTO.getUserName());
        customer.setPassword(customerDTO.getPassword());
        customer.setSdt(customerDTO.getSdt());
        return customer;
    }
}