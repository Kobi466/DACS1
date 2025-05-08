package network;

import java.io.Serializable;

public class JsonResponse implements Serializable {
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID


    private String status;
    private Object data;
    private String source;

    public JsonResponse(String status, Object data, String source) {
        this.status = status;
        this.data = data;
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public String getSource() {
        return source;
    }
}
