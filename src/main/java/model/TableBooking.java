package model;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name="table_booking")
public class TableBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int tables_id;
    @Enumerated(EnumType.STRING)
    private TableType tableType;
    @Enumerated(EnumType.STRING)
    private StatusTable status;
    @OneToMany(mappedBy = "tableBooking")
    private List<Reservation> reservations;

    public enum TableType {
        BÀN, PHÒNGVIP;
    }
    public enum StatusTable {
        TRỐNG, ĐÃĐẶT
    }

    public TableBooking() {
    }

    public TableBooking(TableType tableType, StatusTable status, List<Reservation> reservations) {
        this.tableType = tableType;
        this.status = status;
        this.reservations = reservations;
    }

    public int getTables_id() {
        return tables_id;
    }

    public void setTables_id(int tables_id) {
        this.tables_id = tables_id;
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
                "tables_id=" + tables_id +
                ", tableType=" + tableType +
                ", status=" + status +
                ", reservations=" + reservations +
                '}';
    }
}
