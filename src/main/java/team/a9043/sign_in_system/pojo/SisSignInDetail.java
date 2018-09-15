package team.a9043.sign_in_system.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SisSignInDetail {
    private Integer ssidId;

    private Boolean ssidStatus;

    private Integer ssiId;

    private String suId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SisUser sisUser;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SisSignIn sisSignIn;
}