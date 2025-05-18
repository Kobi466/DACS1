package service;

import dto.CustomerDTO;
import mapper.CustomerMapper;
import org.mapstruct.factory.Mappers;
import repositoy_dao.CustomerDAO;
import model.Customer;

public class CustomerService extends AbstractService<Customer, Integer> {

    private final CustomerDAO customerDAO;
    private final CustomerMapper customerMapper;


    public CustomerService() {
        this.customerDAO = new CustomerDAO();
        this.customerMapper = Mappers.getMapper(CustomerMapper.class); // Sử dụng MapStruct
    }

    public CustomerDTO login(String username, String password) {
        Customer customer = customerDAO.findByUsername(username);
        if (customer != null && customer.getPassword().equals(password)) {
            return customerMapper.toDTO(customer); // Chuyển đổi chính xác từ Customer sang CustomerDTO
        }
        return null;
    }
    public boolean regis(String username, String password, String sdt) {
        Customer customer = CustomerMapper.toEntity(new CustomerDTO(username, password, sdt));
        if(customer!=null){
            customerDAO.insert(customer);
            return true;
        }
        return false;
    }

}
