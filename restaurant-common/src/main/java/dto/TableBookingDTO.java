package dto;

public class TableBookingDTO implements java.io.Serializable{
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tính tương thích khi serialize/deserialize

    private int tablesId;
    private String tableType; // Chuyển kiểu enum thành String
    private String status; // Chuyển kiểu enum thành String

    // Constructor
    public TableBookingDTO(int tablesId, String tableType, String status) {
        this.tablesId = tablesId;
        this.tableType = tableType;
        this.status = status;
    }

    // Getter và Setter
    public int getTablesId() {
        return tablesId;
    }

    public void setTablesId(int tablesId) {
        this.tablesId = tablesId;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
