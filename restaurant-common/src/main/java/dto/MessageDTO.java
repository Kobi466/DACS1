package dto;

import java.time.LocalDateTime;

public class MessageDTO implements java.io.Serializable{
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID để đảm bảo tương thích dữ liệu giữa các phiên bản.
    private String sender;
    private String receiver;
    private String content;
    private String sentAt;
    private Integer customerId;

    // Constructor phù hợp với Hibernate
    public MessageDTO(String sender, String receiver, String content, LocalDateTime sentAt, Integer customerId) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sentAt = sentAt != null ? sentAt.toString() : null; // Chuyển đổi LocalDateTime về String nếu cần
        this.customerId = customerId;
    }

    public MessageDTO() {
    }

    // Getters và Setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}