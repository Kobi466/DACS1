package dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CustomerVoucherDTO implements Serializable {
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tính tương thích khi serialize/deserialize

    private int id;
    private Boolean used;
    private LocalDateTime usedAt;
    private int customerId;  // ID của khách hàng, tránh việc truyền đối tượng Customer
    private int voucherId;   // ID của voucher, tránh việc truyền đối tượng Voucher

    // Constructor
    public CustomerVoucherDTO(int id, Boolean used, LocalDateTime usedAt, int customerId, int voucherId) {
        this.id = id;
        this.used = used;
        this.usedAt = usedAt;
        this.customerId = customerId;
        this.voucherId = voucherId;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }
}
