package team.a9043.sign_in_system_version_2.tokenuser;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenUser {
    boolean required() default true;
}
