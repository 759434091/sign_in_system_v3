package team.a9043.sign_in_system_version_2.pojo.extend;

import com.fasterxml.jackson.annotation.JsonInclude;
import team.a9043.sign_in_system_version_2.pojo.ManAbsRec;
import team.a9043.sign_in_system_version_2.pojo.Schedule;
import team.a9043.sign_in_system_version_2.pojo.SignIn;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleWithHistorySignIn extends Schedule {
    private List<SignIn> signInList;
    private List<ManAbsRec> manAbsRecList;

    public List<ManAbsRec> getManAbsRecList() {
        return manAbsRecList;
    }

    public ScheduleWithHistorySignIn setManAbsRecList(List<ManAbsRec> manAbsRecList) {
        this.manAbsRecList = manAbsRecList;
        return this;
    }

    public List<SignIn> getSignInList() {
        return signInList;
    }

    public ScheduleWithHistorySignIn setSignInList(List<SignIn> signInList) {
        this.signInList = signInList;
        return this;
    }
}
