package team.a9043.sign_in_system.service_pojo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FormId implements Serializable {
    public FormId(String formId, LocalDateTime localDateTime) {
        this.formId = formId;
        this.localDateTime = localDateTime;
    }

    private String formId;
    private LocalDateTime localDateTime;
}
