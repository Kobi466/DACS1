package service;

public class CustomerVoucherService extends AbstractService<model.CustomerVoucher, Integer> {
    public CustomerVoucherService() {
        this.dao = new repositoy_dao.CustomerVoucherDAO();
    }
}
