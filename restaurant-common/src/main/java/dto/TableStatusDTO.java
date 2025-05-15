package dto;

import java.time.LocalDateTime;

public class TableStatusDTO implements java.io.Serializable{
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tính tương thích khi serialize/deserialize

    private int tableId;
    private String tableName;
    private TableType tableType; // BAN hoặc PHONG_VIP
    // Thông tin khách
    private String customerName;
    // Thông tin đặt chỗ gần nhất (nếu có)
    private LocalDateTime reservationTime;
    private ReservationStatus reservationStatus;

    // Thông tin đơn hàng gần nhất (nếu có)
    private LocalDateTime orderTime;
    private OrderSummaryDTO.OrderStatus orderStatus;

    // Số lượng đơn hàng đang hoạt động (giúp hiển thị tooltip đẹp)
    private int activeOrderCount;

    // Tooltip hiển thị khi hover chuột
    private String tooltipText;
    public enum TableType {
        BAN, PHONG_VIP
    }

    private StatusTable status;// TRONG, DA_DAT, etc. (hiển thị màu)
    public enum StatusTable {
        TRONG, DA_DAT, CHO_XAC_NHAN
    }
    public enum ReservationStatus {
        CHO_XAC_NHAN, DA_XAC_NHAN, HUY
    }
    // Constructor, getters, setters
    public TableStatusDTO(int tableId, String tableName, TableType tableType, StatusTable status,
                           String customerName, LocalDateTime reservationTime, ReservationStatus reservationStatus,
                           LocalDateTime orderTime, OrderSummaryDTO.OrderStatus orderStatus, int activeOrderCount) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.tableType = tableType;
        this.status = status;
        this.customerName = customerName;
        this.reservationTime = reservationTime;
        this.reservationStatus = reservationStatus;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.activeOrderCount = activeOrderCount;
    }

    public TableStatusDTO() {
    }

    public int getTableId() {
        return tableId;
    }
    public void setTableId(int tableId) {
        this.tableId = tableId;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public TableType getTableType() {
        return tableType;
    }
    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }
    public StatusTable getStatus() {
        return status;
    }
    public void setStatus(StatusTable status) {
        this.status = status;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public LocalDateTime getReservationTime() {
        return reservationTime;
    }
    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }
    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }
    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
    public LocalDateTime getOrderTime() {
        return orderTime;
    }
    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }
    public OrderSummaryDTO.OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(OrderSummaryDTO.OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    public int getActiveOrderCount() {
        return activeOrderCount;
    }
    public void setActiveOrderCount(int activeOrderCount) {
        this.activeOrderCount = activeOrderCount;
    }
    public String getTooltipText() {
        return tooltipText;
    }
    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }
    @Override
    public String toString() {
        return "TableStatusDTO{" +
                "tableId=" + tableId +
                ", tableName='" + tableName + '\'' +
                ", tableType=" + tableType +
                ", status=" + status +
                ", customerName='" + customerName + '\'' +
                ", reservationTime=" + reservationTime +
                ", reservationStatus=" + reservationStatus +
                ", orderTime=" + orderTime +
                ", orderStatus=" + orderStatus +
                ", activeOrderCount=" + activeOrderCount +
                '}';
    }

}
