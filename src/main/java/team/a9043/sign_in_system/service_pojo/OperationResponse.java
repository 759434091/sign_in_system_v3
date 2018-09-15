package team.a9043.sign_in_system.service_pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * @author a9043
 */
@Setter
@Getter
public class OperationResponse {
    public static final OperationResponse SUCCESS = new OperationResponse(true);

    private boolean success;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int code;

    private OperationResponse(boolean success) {
        this.success = success;
    }

    public OperationResponse() {
    }

    public OperationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
