package wolox.training.factories;

import java.time.LocalDate;
import wolox.training.models.User;

public class UserFactory {
    public static User getUserTroy(){
        return new User(
            "Troy",
            "WonderfulTroy",
            LocalDate.now(),
            "troy's password"
        );
    }

    public static User getUserKaren(){
        return new User(
            "Karen",
            "SillyKaren",
            LocalDate.now(),
            "karen's password"
        );
    }
}
