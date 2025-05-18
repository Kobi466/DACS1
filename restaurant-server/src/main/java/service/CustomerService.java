package service;

import dto.CustomerDTO;
import mapper.CustomerMapper;
import org.mapstruct.factory.Mappers;
import repositoy_dao.CustomerDAO;
import model.Customer;
import security.PasswordService;

public class CustomerService extends AbstractService<Customer, Integer> {

    private final CustomerDAO customerDAO;
    private final CustomerMapper customerMapper;
    private final PasswordService passwordService = new PasswordService();



    public CustomerService() {
        this.customerDAO = new CustomerDAO();
        this.customerMapper = Mappers.getMapper(CustomerMapper.class); // Sử dụng MapStruct
    }

    public CustomerDTO login(String username, String password) {
        Customer customer = customerDAO.findByUsername(username);
        boolean isPasswordValid = passwordService.matches(password, customer.getPassword());
        if (isPasswordValid==true) {
            return customerMapper.toDTO(customer); // Chuyển đổi chính xác từ Customer sang CustomerDTO
        }
        return null;
    }
    public boolean regis(String username, String password, String sdt) {
        String code = passwordService.encodePassword(password);
        Customer customer = CustomerMapper.toEntity(new CustomerDTO(username, code, sdt));
        if(customer!=null){
            customerDAO.insert(customer);
            return true;
        }
        return false;
    }

}
