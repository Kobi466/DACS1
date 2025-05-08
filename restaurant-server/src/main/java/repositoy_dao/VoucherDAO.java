package repositoy_dao;

import model.Voucher;

public class VoucherDAO extends AbstractDAO<Voucher, Integer> {
    public static VoucherDAO getInstance(){
        return new VoucherDAO();
    }
    public VoucherDAO() {
        super(Voucher.class);
    }
}
