package repositoy_dao;

import model.CustomerVoucher;

public class CustomerVoucherDAO extends AbstractDAO<CustomerVoucher, Integer> {
    public static CustomerVoucherDAO getInstance(){
        return new CustomerVoucherDAO();
    }
    public CustomerVoucherDAO() {
        super(CustomerVoucher.class);
    }
}
