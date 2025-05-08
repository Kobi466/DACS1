package service;

public class VoucherService extends AbstractService<model.Voucher, Integer> {
    public VoucherService() {
        this.dao = new repositoy_dao.VoucherDAO();
    }
}
