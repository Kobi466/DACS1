package dto;

import java.time.LocalTime;
import java.util.List;

public class ReservationOrderDTO implements java.io.Serializable{
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tính tương thích khi serialize/deserialize

    private int id;
    private String tableCode;             // Ví dụ: PHONGVIP5
    private LocalTime bookingTime;        // Giờ đặt bàn
    private List<ItemRequest> items;      // Danh sách món

    // Inner class đại diện từng món ăn
    public static class ItemRequest implements java.io.Serializable{
        private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tính tương thích khi serialize/deserialize
        private String itemName;
        private int quantity;

        public ItemRequest() {}

        public ItemRequest(String itemName, int quantity) {
            this.itemName = itemName;
            this.quantity = quantity;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
    // Getter - Setter cho DTO chính
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public List<ItemRequest> getItems() {
        return items;
    }

    public void setItems(List<ItemRequest> items) {
        this.items = items;
    }
}
