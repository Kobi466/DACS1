package network;

public enum CommandType {
    // Các lệnh liên quan đến người dùng
    LOGIN,
    LOGIN_SUCCESS,
    LOGIN_FAIL,
    SEND_MESSAGE,
    MESSAGE_SENT,
    NEW_MESSAGE, // Tin nhắn mới phát real-time
    GET_CHAT_HISTORY,
    CHAT_HISTORY,
    GET_CUSTOMER_LIST,
    GET_CUSTOMER_DETAILS,
    STAFF_JOIN,
    RESERVE_AND_ORDER_SUCCESS,
    RESERVE_AND_ORDER_FAIL,
    RESERVE_AND_ORDER
}
