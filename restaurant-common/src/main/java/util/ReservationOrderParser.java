package util;

import dto.ReservationOrderDTO;
import dto.ReservationOrderDTO.ItemRequest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReservationOrderParser {

    public static ReservationOrderDTO parse(int id, String message) {
        ReservationOrderDTO dto = new ReservationOrderDTO();
        dto.setId(id);

        // Parse booking time
        Pattern timePattern = Pattern.compile("lúc\\s+(\\d{1,2}:\\d{2})");
        Matcher timeMatcher = timePattern.matcher(message);
        if (timeMatcher.find()) {
            dto.setBookingTime(LocalTime.parse(timeMatcher.group(1)));
        }

        // Parse table code
        Pattern tablePattern = Pattern.compile("(PHONGVIP\\d+|BAN\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher tableMatcher = tablePattern.matcher(message);
        if (tableMatcher.find()) {
            dto.setTableCode(tableMatcher.group(1).toUpperCase());
        }

        // Parse food items
        List<ItemRequest> itemList = new ArrayList<>();
        Pattern itemPattern = Pattern.compile("([\\p{L}\\s]+?)\\s*(\\d+)\\s*xuất", Pattern.CASE_INSENSITIVE);
        Matcher itemMatcher = itemPattern.matcher(message);
        while (itemMatcher.find()) {
            String itemName = itemMatcher.group(1).trim().toLowerCase();
            int quantity = Integer.parseInt(itemMatcher.group(2));
            itemList.add(new ItemRequest(itemName, quantity));
        }
        dto.setItems(itemList); // ✅ set danh sách vào DTO

        return dto;
    }
}
