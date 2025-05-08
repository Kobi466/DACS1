package session;

public class SessionManager {
    private static final SessionManager instance = new SessionManager();
    private int customerId = -1; // Default invalid value

    private SessionManager() {
    }

    public static SessionManager instance() {
        return instance;
    }

    public int getCustomerId() {
        System.out.println("🟢 Debug: Lấy giá trị customerId từ SessionManager: " + customerId);
        if (customerId <= 0) {
            System.err.println("⚠️ Warning: customerId không hợp lệ! Giá trị hiện tại: " + customerId);
        }
        return customerId;
    }

    public void setCustomerId(int customerId) {
        System.out.println("✅ Gán ID khách hàng: " + customerId);
        this.customerId = customerId;
    }

    public boolean isLoggedIn() {
        boolean loggedIn = customerId > 0;
        System.out.println("🟢 Debug: Kiểm tra đăng nhập: " + loggedIn);
        return loggedIn;
    }
}