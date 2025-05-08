package dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VoucherDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int voucherId;
    private String code;
    private BigDecimal discount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Integer> customerVoucherIds; // ID của CustomerVoucher, tránh việc truyền toàn bộ đối tượng

    // Constructor
    public VoucherDTO(int voucherId, String code, BigDecimal discount, LocalDateTime startDate, LocalDateTime endDate, List<Integer> customerVoucherIds) {
        this.voucherId = voucherId;
        this.code = code;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerVoucherIds = customerVoucherIds;
    }

    // Getter và Setter
    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<Integer> getCustomerVoucherIds() {
        return customerVoucherIds;
    }

    public void setCustomerVoucherIds(List<Integer> customerVoucherIds) {
        this.customerVoucherIds = customerVoucherIds;
    }
}
