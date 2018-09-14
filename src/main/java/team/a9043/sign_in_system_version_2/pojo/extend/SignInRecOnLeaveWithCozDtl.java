package team.a9043.sign_in_system_version_2.pojo.extend;

import com.fasterxml.jackson.annotation.JsonInclude;
import team.a9043.sign_in_system_version_2.pojo.SignInRec;
import team.a9043.sign_in_system_version_2.pojo.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInRecOnLeaveWithCozDtl extends SignInRec {
    private User user;
    private SignInWithCozDtl signInWithCozDtl;

    public SignInWithCozDtl getSignInWithCozDtl() {
        return signInWithCozDtl;
    }

    public SignInRecOnLeaveWithCozDtl setSignInWithCozDtl(SignInWithCozDtl signInWithCozDtl) {
        this.signInWithCozDtl = signInWithCozDtl;
        return this;
    }

    public User getUser() {
        return user;
    }

    public SignInRecOnLeaveWithCozDtl setUser(User user) {
        this.user = user;
        return this;
    }
}
