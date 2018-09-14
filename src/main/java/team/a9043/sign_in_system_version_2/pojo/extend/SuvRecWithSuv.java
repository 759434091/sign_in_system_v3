package team.a9043.sign_in_system_version_2.pojo.extend;

import com.fasterxml.jackson.annotation.JsonInclude;
import team.a9043.sign_in_system_version_2.pojo.Supervision;
import team.a9043.sign_in_system_version_2.pojo.SuvRec;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuvRecWithSuv extends SuvRec {
    private Supervision supervision;

    public Supervision getSupervision() {
        return supervision;
    }

    public SuvRecWithSuv setSupervision(Supervision supervision) {
        this.supervision = supervision;
        return this;
    }
}
