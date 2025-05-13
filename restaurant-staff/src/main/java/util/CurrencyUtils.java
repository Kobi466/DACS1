package util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    private static final Locale VIETNAM = new Locale("vi", "VN");

    public static String format(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(VIETNAM);
        return formatter.format(amount);
    }
}
