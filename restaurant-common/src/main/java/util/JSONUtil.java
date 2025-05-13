package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.ArrayList;
import java.util.List;

public class JSONUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Trả về instance ObjectMapper dùng chung
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    // Chuyển object bất kỳ thành chuỗi JSON
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    // Chuyển JSON string thành object kiểu T
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Chuyển dữ liệu kiểu Object (thường là Map) sang object kiểu T
    public static <T> T convertValue(Object data, Class<T> clazz) {
        try {
            return objectMapper.convertValue(data, clazz);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Chuyển Object (List<Map<String,Object>>) sang List<T>
    public static <T> List<T> convertToList(Object data, Class<T> clazz) {
        ObjectMapper mapper = getObjectMapper();
        List<T> list = new ArrayList<>();
        if (data instanceof List<?> rawList) {
            for (Object obj : rawList) {
                T item = mapper.convertValue(obj, clazz);
                list.add(item);
            }
        }
        return list;
    }

}
