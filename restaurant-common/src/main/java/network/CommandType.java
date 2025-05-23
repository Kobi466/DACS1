package network;

public enum CommandType {
    // Các lệnh liên quan đến người dùng
    ERROR,
    //Dang nhap
    LOGIN,
    LOGIN_SUCCESS,
    LOGIN_FAIL,
    REGISTER_SUCCESS,
    REGISTER_FAIL,
    REGISTER,
    //Chat
    SEND_MESSAGE,
    SEND_SUCCESS,
    NEW_MESSAGE,//tin nhan real time
    //danh sach khach hang
    GET_CUSTOMER_LIST_WITH_MESSAGES,
    GET_CUSTOMERS_WITH_MESSAGES,
    NO_CUSTOMERS_WITH_MESSAGES,
    GET_CHAT_HISTORY,
    CHAT_HISTORY_SUCCESS,
    GET_CUSTOMER_LIST,
    GET_CUSTOMER_DETAILS,
    STAFF_JOINED,
    RESERVE_AND_ORDER_SUCCESS,
    RESERVE_AND_ORDER_FAIL,
    RESERVE_AND_ORDER,
    GET_ORDERS,
    GET_ORDERS_FAIL,
    GET_ORDERS_SUCCESS,
    GET_ORDER_ITEMS,
    GET_ORDER_ITEMS_SUCCESS,
    GET_ORDER_ITEMS_FAIL,
    NEW_ORDER_CREATED,
    UPDATE_ORDER_STATUS,
    UPDATE_ORDER_STATUS_SUCCESS,
    UPDATE_ORDER_STATUS_FAIL,
    GET_ALL_TABLE_STATUS,
    UPDATE_TABLE_STATUS,
    UPDATE_TABLE_STATUS_SUCCESS,
    UPDATE_TABLE_STATUS_FAIL,
}
