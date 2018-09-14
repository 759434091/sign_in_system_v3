package team.a9043.sign_in_system_version_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SignInSystemVersion2Application{

    public static void main(String[] args) {
        SpringApplication.run(SignInSystemVersion2Application.class, args);
    }
}
