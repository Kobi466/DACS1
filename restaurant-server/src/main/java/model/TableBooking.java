package model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "table_booking")
public class TableBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "table_name", unique = true, nullable = false)
    private String tableName;

    @Enumerated(EnumType.STRING)
    private TableType tableType;

    @Enumerated(EnumType.STRING)
    private StatusTable status;

    @OneToMany(mappedBy = "tableBooking")
    private List<Reservation> reservations;

    public enum TableType {
        BAN, PHONG_VIP;
    }

    public enum StatusTable {
        TRONG, DA_DAT;
    }

    public TableBooking() {}

    public TableBooking(String tableName, TableType tableType, StatusTable status) {
        this.tableName = tableName;
        this.tableType = tableType;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "TableBooking{" +
                "id=" + id +
                ", tableName='" + tableName + '\'' +
                ", tableType=" + tableType +
                ", status=" + status +
                '}';
    }
}
