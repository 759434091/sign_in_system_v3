package team.a9043.sign_in_system_version_2.pojo.extend;

import com.fasterxml.jackson.annotation.JsonInclude;
import team.a9043.sign_in_system_version_2.pojo.SignIn;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInWithCozDtl extends SignIn {
    private ScheduleWithCozDtl scheduleWithCozDtl;

    public ScheduleWithCozDtl getScheduleWithCozDtl() {
        return scheduleWithCozDtl;
    }

    public SignInWithCozDtl setScheduleWithCozDtl(ScheduleWithCozDtl scheduleWithCozDtl) {
        this.scheduleWithCozDtl = scheduleWithCozDtl;
        return this;
    }
}
