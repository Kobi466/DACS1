package dao;

import model.Voucher;

import java.util.List;

public class VoucherDAO extends AbstractDAO<Voucher, Integer> {
    public static VoucherDAO getInstance(){
        return new VoucherDAO();
    }
    public VoucherDAO() {
        super(Voucher.class);
    }
    public Voucher findByCode(String code) {
        return em.createQuery("from Voucher where code =:code", Voucher.class).setParameter("code", code).getSingleResult();
    }
}
