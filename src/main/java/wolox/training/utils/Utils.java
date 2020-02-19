package wolox.training.utils;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;

public class Utils {
    public static String emptyErrorMessage(String identifier){
        return String.format("%s cannot be empty!", identifier);
    }

    public static String nullErrorMessage(String identifier){
        return String.format("%s cannot be null!", identifier);
    }

    public static LocalDate checkLocalDate(LocalDate date, String identifier){
        checkArgument(date != null, nullErrorMessage(identifier));
        return date;
    }

    public static String checkString(String str, String identifier) {
        checkArgument(str != null, nullErrorMessage(identifier));
        checkArgument( !str.isEmpty(), emptyErrorMessage(identifier));
        return str;
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
