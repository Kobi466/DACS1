package network;


import java.io.Serializable;

public class JsonRequest implements Serializable {
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID

    private String command;
    private Object data;
    private String sender;

    public JsonRequest() {
    }

    public JsonRequest(String command, Object data, String sender) {
        this.command = command;
        this.data = data;
        this.sender = sender;
    }

    public JsonRequest(String command, Object data) {
        this.command = command;
        this.data =  data;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData( Object data) {
        this.data = data;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
