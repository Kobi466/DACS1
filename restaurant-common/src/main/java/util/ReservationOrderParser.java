package util;

import dto.ReservationOrderDTO;
import dto.ReservationOrderDTO.ItemRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReservationOrderParser {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    public static ReservationOrderDTO parse(int id, String message) {
        ReservationOrderDTO dto = new ReservationOrderDTO();
        dto.setId(id);

        LocalDate date = LocalDate.now(); // mặc định hôm nay
        LocalTime time = null;

        // Parse ngày (ví dụ: "ngày 15/05")
        Pattern datePattern = Pattern.compile("ngày\\s+(\\d{1,2}/\\d{1,2})", Pattern.CASE_INSENSITIVE);
        Matcher dateMatcher = datePattern.matcher(message);
        if (dateMatcher.find()) {
            String dateStr = dateMatcher.group(1);
            try {
                date = LocalDate.parse(dateStr, DATE_FORMAT);
                // Nếu năm không có, thêm năm hiện tại
                date = date.withYear(LocalDate.now().getYear());
            } catch (Exception e) {
                System.err.println("Lỗi parse ngày: " + e.getMessage());
            }
        } else if (message.toLowerCase().contains("ngày mai")) {
            date = LocalDate.now().plusDays(1);
        }

        // Parse giờ (ví dụ: "lúc 18:30")
        Pattern timePattern = Pattern.compile("lúc\\s+(\\d{1,2}:\\d{2})");
        Matcher timeMatcher = timePattern.matcher(message);
        if (timeMatcher.find()) {
            try {
                time = LocalTime.parse(timeMatcher.group(1), TIME_FORMAT);
            } catch (Exception e) {
                System.err.println("Lỗi parse giờ: " + e.getMessage());
            }
        }

        // Nếu có đủ ngày và giờ → set bookingTime
        if (time != null) {
            dto.setBookingTime(LocalTime.from(LocalDateTime.of(date, time)));
        }

        // Parse mã bàn
        Pattern tablePattern = Pattern.compile("(PHONGVIP\\d+|BAN\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher tableMatcher = tablePattern.matcher(message);
        if (tableMatcher.find()) {
            dto.setTableCode(tableMatcher.group(1).toUpperCase());
        }

        // Parse món ăn
        List<ItemRequest> itemList = new ArrayList<>();
        Pattern itemPattern = Pattern.compile("([\\p{L}\\s]+?)\\s*(\\d+)\\s*xuất", Pattern.CASE_INSENSITIVE);
        Matcher itemMatcher = itemPattern.matcher(message);
        while (itemMatcher.find()) {
            String itemName = itemMatcher.group(1).trim().toLowerCase();
            int quantity = Integer.parseInt(itemMatcher.group(2));
            itemList.add(new ItemRequest(itemName, quantity));
        }
        dto.setItems(itemList);

        return dto;
    }
}
